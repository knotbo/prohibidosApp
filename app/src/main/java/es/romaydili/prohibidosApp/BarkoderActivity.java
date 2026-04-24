package es.romaydili.prohibidosApp;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.barkoder.Barkoder;
import com.barkoder.BarkoderConfig;
import com.barkoder.BarkoderHelper;
import com.barkoder.BarkoderView;
import com.barkoder.interfaces.BarkoderResultCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class BarkoderActivity extends AppCompatActivity implements BarkoderResultCallback {

    // --- AQUÍ DEBES DECLARARLOS ---
    private Bitmap mainBitmap;      // La imagen completa de la captura
    private Bitmap documentBitmap;  // El recorte del documento (DNI)
    private Bitmap signatureBitmap; // El recorte de la firma (si el SDK lo extrae)
    private Bitmap pictureBitmap;   // El recorte de la FOTO de la cara

    private BarkoderView bkdView;
    private TextView textViewResult;
    private TextView textViewResultExtras;
    private ImageView thumbNailMRZPicture;
    private ImageView thumbNailMRZDocument;
    private FloatingActionButton buttonScanning, buttonFlash;
    private boolean isScanningActive = false;
    private boolean isFlashActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkoder);

        // 1. Inicializar las vistas del layout [cite: 90, 91]
        bkdView = findViewById(R.id.barkoderView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResultExtras = findViewById(R.id.textViewResultExtras);
        thumbNailMRZPicture = findViewById(R.id.thumbNailMRZPicture);
        thumbNailMRZDocument = findViewById(R.id.thumbNailMRZDocument);
        buttonScanning = findViewById(R.id.buttonScanning);
        buttonFlash = findViewById(R.id.buttonFlash);

        // 2. Configurar el SDK de Barkoder con tu clave de licencia [cite: 94, 95]
        createBarkoderConfig();


        // 3. Configurar qué queremos escanear (QR y DNI/MRZ)
        configureBarkoderSettings();

        // 4. Botón para iniciar o detener el escaneo
        buttonScanning.setOnClickListener(v -> toggleScanning());

        // 5. Botón para iniciar o detener el Flash
        buttonFlash.setOnClickListener(v -> toggleFlash());

        toggleScanning();
    }

    private void createBarkoderConfig(){
        bkdView.config = new BarkoderConfig(this, "PEmBIohr9EZXgCkySoetbwP4gvOfMcGzgxKPL2X6uqPgt9Q9z6t7YP8zV_KOTXr3AEEvXNk6TQ-XNUWzZDJHv-TJ52UFEMZmA1n7jpIVcYev1x85Zp0lwKEiKK5twXsYsLdsgIV20QuvPpu7ZVGbyvhRemrXRulnlEw75EVK49kPJ7H1KPhB-4mKa-2SEkPhyYkEUNA1ENmCywYL3cuRdpshohZdgpWnl28LcCrBliUvR5-jkRY3_q45qptFLhkqieeQRyS93RN4d1TxMZ0ZJTEgHTkJs92Sy-sHNUuzLNM.", result -> {
            Log.i("LicenseInfo", result.message); // [cite: 97]
            //return null;
        });
    }


    private void configureBarkoderSettings() {
        if (bkdView.config != null) {
            // Obtenemos el objeto de configuración de decodificadores [cite: 94]
            Barkoder.Config decoderConfig = bkdView.config.getDecoderConfig();

            // En BarKoder Android, la activación se hace asignando el valor directamente.
            decoderConfig.QR.enabled = true;
            decoderConfig.IDDocument.enabled = true;
            decoderConfig.encodingCharacterSet = "ISO-8859-1"; //Antes no existia, lo usamos para que el QR de midni coincide con el del ejemeplo: dc 03 75 81


            // Configuración de feedback
            bkdView.config.setVibrateOnSuccessEnabled(true);
            bkdView.config.setBeepOnSuccessEnabled(MainActivity.getBeep());

            // --- ZONA DE PRUEBAS---
            bkdView.config.getScanningIndicatorColor();
            bkdView.config.setImageResultEnabled(true);
            bkdView.config.setRegionOfInterestVisible(true);
            bkdView.config.setScanningIndicatorAlwaysVisible(true);
            // ----------------------------


            // Región de interés (0f a 100f)
            bkdView.config.setRegionOfInterest(0f, 0f, 100f, 100f);

        }
    }

    private void toggleScanning() {
        if (isScanningActive) {
            bkdView.stopScanning();
            isScanningActive = false;
        } else {
            // Iniciar el proceso de escaneo
            bkdView.startScanning(this); // [cite: 112]
            isScanningActive = true;
        }
    }

    private void toggleFlash() {
        if (isFlashActive) {
            bkdView.setFlashEnabled(false);
            isFlashActive = false;
            buttonFlash.setImageResource(R.drawable.amrz_ic_ico_flash_off);
        }else{
            bkdView.setFlashEnabled(true);
            isFlashActive = true;
            buttonFlash.setImageResource(R.drawable.amrz_ic_flash_on);
        }
    }

    // 5. Implementación del callback de resultados [cite: 103, 106]
    @Override
    public void scanningFinished(Barkoder.Result[] results, Bitmap[] thumbnails, Bitmap imageResult) {
        if (results == null || results.length == 0) {
            Log.e("BARKODER", "Escaneo finalizado sin resultados.");
            return;
        }

        // 1. OBTENEMOS EL RESULTADO PRINCIPAL
        Barkoder.Result res = results[0];

        // --- FILTRO ANTIFANTASMAS CON PAUSA ---
        if (res.textualData != null && res.textualData.contains("is not licensed")) {
            Log.e("BARKODER", "Licencia de ID no activa. Pausando motor...");

            // 1. Detenemos el escaneo inmediatamente
            bkdView.stopScanning();

            // 2. Esperamos 2 segundos antes de volver a permitir el escaneo
            // Esto evita que entre en bucle si el móvil sigue en la mesa
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Log.i("BARKODER", "Reiniciando motor tras pausa de seguridad");
                bkdView.startScanning(this);
            }, 2000);

            return; // Salimos para no ejecutar el resto del código
        }

        // 2. INSPECCIÓN DE METADATOS "EXTRA" (Por si el binario puro está escondido ahí)
        /*
        Log.d("BARKODER_DEBUG", "=== BUSCANDO BINARIO EN METADATOS EXTRA ===");
        if (res.extra != null) {
            for (Barkoder.BKKeyValue kv : res.extra) {
                Log.d("BARKODER_DEBUG", "Key: " + kv.key + " | Value: " + kv.value);
            }
        }
         */

        // 3. ESTRATEGIA DE RESCATE DE BYTES
        // Intentamos recuperar los bytes usando ISO-8859-1 porque es la única que no
        // altera los valores de los bytes individuales (mapeo 1:1).
        byte[] rescueBytes;
        if (res.textualData != null) {
            rescueBytes = res.textualData.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        } else {
            rescueBytes = res.binaryData; // Fallback al binario por defecto
        }

        // 4. GUARDADO EN EL HOLDER PARA LA MAIN_ACTIVITY
        ScanDataHolder.rawBarcodeData = rescueBytes;

        // Log de verificación visual del inicio del buffer (Buscando el dc 03)
        if (rescueBytes != null) {
            StringBuilder sb = new StringBuilder();
            // Definimos cuántos bytes queremos ver (ej: 128)
            int bytesAVer = Math.min(rescueBytes.length, 128);

            for (int j = 0; j < bytesAVer; j++) {
                sb.append(String.format("%02x ", rescueBytes[j] & 0xFF));
                // Opcional: Añadir un guion cada 8 bytes para que sea más legible
                if ((j + 1) % 8 == 0 && j != bytesAVer - 1) sb.append("- ");
            }

            Log.d("BARKODER_FINAL", "Buffer (L:" + rescueBytes.length + "): " + sb.toString());
        }

        /*
        if (res.images != null) {
            Log.d("BARKODER_FULL", "Número de imágenes: " + res.images.length);
            for (Barkoder.BKImageDescriptor img : res.images) {
                Log.d("BARKODER_FULL", "Imagen hallada: " + img.name + " (" + img.image.getWidth() + "x" + img.image.getHeight() + ")");
            }
        }
        */

        // 5. PROCESAMIENTO DEL JSON PARA LA UI
        runOnUiThread(() -> {
            try {
                JSONObject scanResult = new JSONObject();
                scanResult.put("text", res.textualData);
                scanResult.put("type", res.barcodeTypeName);
                scanResult.put("characterSet", res.characterSet);

                // Incluso puedes meter los "extras" (Nombre, apellidos, etc. si es un DNI)
                if (results[0].extra != null) {
                    JSONObject extras = new JSONObject();
                    for (Barkoder.BKKeyValue item : results[0].extra) {
                        extras.put(item.key, item.value);
                    }
                    scanResult.put("details", extras);
                }

                if (res.images != null) {
                    for (Barkoder.BKImageDescriptor imgDesc : res.images) {
                        // La imagen "document" es la que contiene el recorte del DNI
                        if (imgDesc.name.equals("main") && imgDesc.image != null) {

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            // Comprimimos un poco para no saturar el Intent (máximo 1MB)
                            imgDesc.image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                            byte[] b = baos.toByteArray();
                            String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);

                            // Inyectamos en el JSON para que procesarLecturaMRZ la vea
                            scanResult.put("image_mrz", imageEncoded);
                            //Log.d("BARKODER_FOTO", "Imagen 'document' inyectada con éxito");
                            break;
                        }
                    }
                }

                // Enviamos el JSON a la función que cierra la actividad y vuelve al MainActivity
                // Nota: El parsing real (Nombre, DNI) se hace en el MainActivity al recibir estos bytes
                sendResultMrz(scanResult);

            } catch (Exception e) {
                Log.e("BARKODER", "Error al empaquetar JSON: " + e.getMessage());
                finish(); // Cerramos para no bloquear la App
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isScanningActive) {
            bkdView.stopScanning();
        }
    }

    private void sendResultMrz(JSONObject successfulMrzScan) {
        try {
            // Si documentBitmap tiene el recorte del MRZ, lo convertimos
            if (mainBitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // Comprimimos un poco para que el Intent no sufra (calidad 80)
                mainBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                byte[] bytes = out.toByteArray();
                String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                // Lo metemos en el JSON con una clave nueva
                successfulMrzScan.put("image_mrz", base64Image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Limpieza de memoria antes de cerrar
        mainBitmap = null;
        documentBitmap = null;
        pictureBitmap = null;
        signatureBitmap = null;

        Intent intent = getIntent();
        intent.putExtra("key", successfulMrzScan.toString());
        setResult(RESULT_OK, intent);

        finish();
    }
}