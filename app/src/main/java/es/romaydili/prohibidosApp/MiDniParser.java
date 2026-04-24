package es.romaydili.prohibidosApp;

import org.json.JSONObject;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MiDniParser {

    public static JSONObject parsear(byte[] bytes) {
        JSONObject resultado = new JSONObject();
        if (bytes == null || bytes.length < 38) return resultado;

        try {
            // 0x00: Magic y Versión
            resultado.put("magic", String.format("%02X", bytes[0]));
            resultado.put("version", bytes[1] & 0xFF);

            // 0x02: País expedidor (C40 de 75 81)
            resultado.put("issuing_country", decodeC40(Arrays.copyOfRange(bytes, 2, 4)).trim()); //pais expedidor: Siempre ES

            // 0x04: Identificador del firmante (v bytes)
            // Primero decodificamos los 4 bytes iniciales (75 9e a9 b5) -> "ESPN20"
            String headerV = decodeC40(Arrays.copyOfRange(bytes, 4, 8)); // Debería dar "ESPN20"
            resultado.put("id_firmante_pais", headerV.substring(0, 2));   // "ES"
            resultado.put("id_firmante_entidad", headerV.substring(2, 4)); // "PN"

            // El tamaño de la referencia en hex (el "20" de ESPN20)
            String hexLenRef = headerV.substring(4, 6); // "20"
            int refLenBytesParsed = Integer.parseInt(hexLenRef, 16); // 32 bytes

            // Cálculo del PDF: Para 32 bytes decodificados se usaron 22 bytes en el QR
            int bytesToReadC40 = ((refLenBytesParsed + 2) / 3) * 2;

            // Decodificamos la referencia (los siguientes 22 bytes)
            byte[] refC40Bytes = Arrays.copyOfRange(bytes, 8, 8 + bytesToReadC40);
            resultado.put("cert_serial_number", decodeC40(refC40Bytes).trim());

            // --- FECHAS Y CATEGORÍA (Posiciones tras v) ---
            int offsetData = 8 + bytesToReadC40;

            // Fecha emisión (3 bytes) y Fecha firma (3 bytes)
            resultado.put("raw_fecha_emision", bytesToHex(Arrays.copyOfRange(bytes, offsetData, offsetData + 3)));
            resultado.put("raw_fecha_firma", bytesToHex(Arrays.copyOfRange(bytes, offsetData + 3, offsetData + 6)));

            // 0x0A+v: Referencia a la definición de los elementos
            int refDef = bytes[offsetData + 6] & 0xFF;

            String labelVerificacion;
            if (refDef >= 0 && refDef < TIPOS_VERIFICACION.length) {
                labelVerificacion = TIPOS_VERIFICACION[refDef];
            } else {
                labelVerificacion = "Tipo " + refDef + " (No definido)";
            }

            resultado.put("tipo_verificacion_label", labelVerificacion);
            resultado.put("tipo_verificacion_id", refDef); // Guardamos el número por si acaso

            resultado.put("categoria_doc", bytes[offsetData + 7] & 0xFF);

            //Fin de tratamiento de la cabecera, comenzamos con los tags


            // --- NUEVO INICIO DE TAGS ---

            // Según el manual, tras 'categoria_doc' (que es offsetData + 7),
            // empiezan los datos firmados.
            // En tu log: offsetData+7 es la posición 37.
            // Por tanto, el primer Tag DEBE empezar en i = 38.

            int i = offsetData + 8; // Posición exacta tras Categoría

            // Forzamos la sincronización: si en i no hay un tag conocido, buscamos el 0x40
            if ((bytes[i] & 0xFF) != 0x40) {
                for (int k = offsetData; k < offsetData + 20; k++) {
                    if ((bytes[k] & 0xFF) == 0x40 && (bytes[k+1] & 0xFF) <= 15) {
                        i = k;
                        break;
                    }
                }
            }

            Log.i("PARSER", "Iniciando bucle TLV en posición: " + i);

            while (i < bytes.length) {
                int tag = bytes[i] & 0xFF;
                i++;

                // IMPORTANTE: Si el tag es 0x80 o superior (como 0xFF de la firma),
                // a veces se usan dos bytes para el tag, pero en este QR son de 1 byte.

                if (i >= bytes.length) break;

                // LECTURA DE LONGITUD (Aquí es donde se suele romper)
                int longitud = 0;
                int firstLenByte = bytes[i] & 0xFF;
                i++;

                if (firstLenByte <= 127) {
                    longitud = firstLenByte;
                } else if (firstLenByte == 0x81) {
                    longitud = bytes[i] & 0xFF;
                    i++;
                } else if (firstLenByte == 0x82) {
                    longitud = ((bytes[i] & 0xFF) << 8) | (bytes[i + 1] & 0xFF);
                    i += 2;
                }

                // --- SEGURIDAD: Si la longitud es absurda, abortamos este tag ---
                if (longitud < 0 || i + longitud > bytes.length) {
                    Log.e("PARSER", "Longitud inválida para tag " + Integer.toHexString(tag));
                    break;
                }

                // EXTRACCIÓN DE DATOS SEGURA (Paso previo al switch)
                byte[] data = Arrays.copyOfRange(bytes, i, i + longitud);

                switch (tag) {
                    case 0x40: resultado.put("document_number", new String(data, StandardCharsets.ISO_8859_1).trim()); break;
                    case 0x42:
                        String rawNac = new String(data, StandardCharsets.ISO_8859_1).trim();
                        // 1. Calculamos edad con el dato bruto
                        int edadCalculada = calcularEdad(rawNac);
                        // 2. Guardamos la edad
                        if (edadCalculada != -1) {
                            resultado.put("age", edadCalculada);
                            if (!resultado.has("is_adult")) resultado.put("is_adult", edadCalculada >= 18);
                        }
                        // 3. Normalizamos la fecha para el servidor PHP
                        resultado.put("date_of_birth", normalizarFechaAISO(rawNac));
                        break;
                    case 0x44: resultado.put("first_name", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x46: resultado.put("last_name", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x48: resultado.put("gender", new String(data, StandardCharsets.ISO_8859_1).trim()); break;
                    case 0x4C: // FECHA CADUCIDAD -> Normalizar a YYYY-MM-DD
                        resultado.put("date_of_expiry", normalizarFechaAISO(new String(data, StandardCharsets.ISO_8859_1).trim()));
                        break;
                    case 0x50: // FOTO
                        resultado.put("image_mrz", Base64.encodeToString(data, Base64.NO_WRAP));
                        break;
                    case 0x5E: // FIRMA
                        resultado.put("signature_raw", bytesToHex(data));
                        break;
                    case 0x60: resultado.put("direccion_completa", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x72: // lugar de domicilio, linea 1
                        resultado.put("lugar_domicilio_l1", new String(data, StandardCharsets.UTF_8).trim());
                        break;
                    case 0x74: // lugar de domicilio, linea 2
                        resultado.put("lugar_domicilio_l2", new String(data, StandardCharsets.UTF_8).trim());
                        break;
                    case 0x76: // lugar de domicilio, linea 1
                        resultado.put("lugar_domicilio_l3", new String(data, StandardCharsets.UTF_8).trim());
                        break;
                    case 0x62: resultado.put("lugar_nacimiento_l1", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x78: resultado.put("lugar_nacimiento_l2", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x7A: resultado.put("lugar_nacimiento_l3", new String(data, StandardCharsets.UTF_8).trim()); break;
                    case 0x64: resultado.put("nationality", new String(data, StandardCharsets.ISO_8859_1).trim()); break;
                    case 0x66: // PADRES
                        resultado.put("parents_names", new String(data, StandardCharsets.UTF_8).trim());
                        break;
                    case 0x68: resultado.put("numero_soporte", new String(data, StandardCharsets.ISO_8859_1).trim()); break;
                    case 0x70: // MAYORÍA DE EDAD
                        boolean esMayorEdad = (data[0] & 0xFF) == 0x01;
                        resultado.put("is_adult", esMayorEdad);
                        break;
                    case 0x80: // TIMESTAMP QR -> Normalizar a YYYY-MM-DD HH:MM:SS
                        String rawTime = new String(data, StandardCharsets.ISO_8859_1).trim();
                        resultado.put("timestampQR", normalizarDateTimeAISO(rawTime));
                        //Comprobamos si es un QR Válido
                        resultado.put("QR_valido", esQRValido(rawTime));
                        break;
                }
                i += longitud; // Saltamos al siguiente tag
            }
        } catch (Exception e) {
            Log.e("PARSER", "Error decodificando: " + e.getMessage());
        }
        return resultado;
    }

    private static String decodeC40(byte[] input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length - 1; i += 2) {
            int val = ((input[i] & 0xFF) << 8) | (input[i + 1] & 0xFF);
            val -= 1;
            int c1 = val / 1600;
            int c2 = (val % 1600) / 40;
            int c3 = (val % 1600) % 40;
            sb.append(parseC40Char(c1)).append(parseC40Char(c2)).append(parseC40Char(c3));
        }
        return sb.toString();
    }

    private static char parseC40Char(int c) {
        if (c == 3) return ' ';
        if (c >= 4 && c <= 13) return (char) (c + 44); // 0-9
        if (c >= 14 && c <= 39) return (char) (c + 51); // A-Z
        return ' ';
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    // Descripciones de los tipos de verificación según PDF Sección 3.1
    private static final String[] TIPOS_VERIFICACION = {
            "Desconocido",          // 0
            "Desconocido",          // 1
            "Desconocido",          // 2
            "Desconocido",          // 3
            "Desconocido",          // 4
            "Desconocido",          // 5
            "Desconocido",          // 6
            "Verificación Simple",  // 7
            "Verificación Completa",// 8
            "Verificación de Edad"  // 9
    };

    private static boolean esQRValido(String timestampQR) {
        try {
            // Formato: 20-04-2026 11:20:00 (Ejemplo basado en tu hora actual)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            java.util.Date fechaExpira = sdf.parse(timestampQR);
            java.util.Date ahora = new java.util.Date();

            // 1. ¿YA HA CADUCADO?
            // Si 'ahora' es después de 'fechaExpira', el QR es basura.
            if (ahora.after(fechaExpira)) {
                Log.w("VALIDACION", "QR caducado. Expira: " + timestampQR + " | Ahora: " + ahora);
                return false;
            }

            // 2. ¿ES DEMASIADO LARGO? (Protección contra manipulación)
            // Si el QR dice que caduca dentro de más de 6 minutos, es sospechoso
            // (Damos 6 min en lugar de 5 por posibles desfases de segundos entre relojes)
            long seisMinutosMs = 6 * 60 * 1000L;
            long diferenciaMs = fechaExpira.getTime() - ahora.getTime();

            if (diferenciaMs > seisMinutosMs) {
                Log.w("VALIDACION", "QR inválido: Tiempo de vida excedido o reloj desincronizado.");
                return false;
            }

            // 3. ¿VIENE DEL FUTURO? (Reloj del móvil muy atrasado)
            // Si la diferencia es negativa (pero el parse no falló), algo raro pasa.
            // Aunque ahora.after ya lo cubre, esto es para lógica de negocio.

            return true;
        } catch (Exception e) {
            Log.e("VALIDACION", "Error parseando fecha: " + timestampQR);
            return false;
        }
    }

    private static int calcularEdad(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) return -1;

        // Quitamos cualquier separador (guiones, barras) para trabajar solo con los números
        // "19-05-1984" -> "19051984"
        String limpia = fechaNacimiento.replaceAll("[^0-9]", "");

        // Un DNI siempre debe tener 8 dígitos de fecha
        if (limpia.length() != 8) return -1;

        try {
            // Según el manual DD-MM-YYYY:
            int dia = Integer.parseInt(limpia.substring(0, 2)); // "19"
            int mes = Integer.parseInt(limpia.substring(2, 4)); // "05"
            int anio = Integer.parseInt(limpia.substring(4, 8)); // "1984"

            java.util.Calendar hoy = java.util.Calendar.getInstance();
            int anioActual = hoy.get(java.util.Calendar.YEAR);
            int mesActual = hoy.get(java.util.Calendar.MONTH) + 1; // Enero es 0
            int diaActual = hoy.get(java.util.Calendar.DAY_OF_MONTH);

            int edad = anioActual - anio;

            // Ajuste de precisión: si no ha llegado su cumple este año, restamos uno
            if (mesActual < mes || (mesActual == mes && diaActual < dia) ) {
                edad--;
            }

            return edad;
        } catch (Exception e) {
            Log.e("EDAD", "Error al procesar: " + fechaNacimiento);
            return -1;
        }
    }

    private static String normalizarFechaAISO(String fechaRaw) {
        if (fechaRaw == null) return "";
        // Quitar cualquier separador para tener solo números
        String limpia = fechaRaw.replaceAll("[^0-9]", "");
        if (limpia.length() < 8) return fechaRaw;
        // De DDMMYYYY a YYYY-MM-DD
        return limpia.substring(4, 8) + "-" + limpia.substring(2, 4) + "-" + limpia.substring(0, 2);
    }

    private static String normalizarDateTimeAISO(String dateTimeRaw) {
        if (dateTimeRaw == null || dateTimeRaw.length() < 10) return dateTimeRaw;
        try {
            // dateTimeRaw suele ser "DD-MM-YYYY HH:mm:ss"
            String fecha = normalizarFechaAISO(dateTimeRaw.substring(0, 10));
            String hora = dateTimeRaw.substring(10).trim();
            return fecha + (hora.isEmpty() ? "" : " " + hora);
        } catch (Exception e) {
            return dateTimeRaw;
        }
    }
}