package es.romaydili.prohibidosApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.print.PrintManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.scansolutions.mrzscannerlib.MRZScanner;

import org.json.JSONException;
import org.json.JSONObject;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//import javax.crypto.Cipher;
import static android.content.ContentValues.TAG;

import com.gemalto.jp2.JP2Decoder;



public class ScannedBarkoderActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue requestQueue;
    public static final int REQUEST_CODE = 1;
    private int clicks = 0;
    private String imagenMRZBase64 = "";
    private String imagenTipo = "";

    Button scanBtn,backBtn,submitBtn,manualBtn;

    EditText editGivenName, surName, editDocNum, editIssuingCount, editNationallity, editDateOfBirth, editAge, editSex, editExporationDate, editOptionalVal,editIssuingDate, editTipoDocumento, editRawMrz,editSDK;
    TextView datosCliente;
    ImageView imgMrz;

    JSONObject JsonFinalUnificado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        datosCliente = findViewById(R.id.activity_description_txt);
        scanBtn = findViewById(R.id.scan_button);
        manualBtn = findViewById(R.id.manual_button);
        submitBtn = findViewById(R.id.submit_btn);
        backBtn = findViewById(R.id.back_btn);
        editGivenName = findViewById(R.id.edit_given_name);
        surName = findViewById(R.id.edit_surname);
        editDocNum = findViewById(R.id.edit_document_number);
        editIssuingCount = findViewById(R.id.edit_issuing_country);
        editNationallity = findViewById(R.id.edit_nationality);
        editDateOfBirth = findViewById(R.id.edit_date_of_birth);
        editAge = findViewById(R.id.edit_age);
        editSex = findViewById(R.id.edit_sex);
        editExporationDate = findViewById(R.id.edit_expiration_date);
        editOptionalVal = findViewById(R.id.edit_optional_values);
        editIssuingDate = findViewById(R.id.edit_issuing_date);
        editTipoDocumento = findViewById(R.id.edit_tipo_documento);
        editRawMrz = findViewById(R.id.edit_raw_mrz);
        editSDK = findViewById(R.id.edit_sdk);
        imgMrz = findViewById(R.id.imgMrzResult);
//      imgBtnFront=findViewById(R.id.imageButton_scanImage_front);
//      imgBtnBack=findViewById(R.id.imageButton_scanImage_back);

        scanBtn.setOnClickListener(this);
        manualBtn.setOnClickListener(this);
//        imgBtnBack.setOnClickListener(this);
//        imgBtnFront.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        datosCliente.setOnClickListener(this);

        startScanner();
    }

    private void cleanEditText(){
        editGivenName.setText("");
        surName.setText("");
        editDocNum.setText("");
        editIssuingCount.setText("");
        editNationallity.setText("");
        editDateOfBirth.setText("");
        editAge.setText("");
        editSex.setText("");
        editIssuingDate.setText("");
        editExporationDate.setText("");
        editOptionalVal.setText("");
        editTipoDocumento.setText("");
        editRawMrz.setText("");

        // Foto
        imgMrz.setImageBitmap(null);
    }

    private void addResultToEditText(JSONObject datos) throws JSONException {

        Log.i(TAG, "Datos Obtenidos de BarKoder:\n" + datos);

        editGivenName.setText(datos.optString("nombre"));
        surName.setText(datos.optString("apellidos"));
        editDocNum.setText(datos.optString("dni"));
        editIssuingCount.setText(datos.optString("pais_emisor"));
        editNationallity.setText(datos.optString("nacionalidad"));
        editDateOfBirth.setText(datos.optString("fecha_nacimiento"));
        editAge.setText(datos.optString("edad"));
        editSex.setText(datos.optString("sexo"));
        editIssuingDate.setText(datos.optString("fecha_expedicion"));
        editExporationDate.setText(datos.optString("fecha_caducidad"));
        editOptionalVal.setText(datos.optString("valores_opcionales"));
        editTipoDocumento.setText(datos.optString("tipo"));
        editRawMrz.setText(datos.optString("raw"));

        // Foto
        imagenMRZBase64 = datos.getString("foto");
        mostrarFoto(imagenMRZBase64);


        if(MainActivity.getDebugMode() == true){

//            findViewById(R.id.label_sdk).setVisibility(View.VISIBLE);
//            editSDK.setVisibility(View.VISIBLE);

            findViewById(R.id.txt_date_of_birth).setVisibility(View.VISIBLE);
            editDateOfBirth.setVisibility(View.VISIBLE);

            findViewById(R.id.txt_tipo_documento).setVisibility(View.VISIBLE);
            editTipoDocumento.setVisibility(View.VISIBLE);

            findViewById(R.id.label_mrz).setVisibility(View.VISIBLE);
            editRawMrz.setVisibility(View.VISIBLE);

            findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);
            editGivenName.setVisibility(View.VISIBLE);

            findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
            surName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
            editIssuingCount.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
            editIssuingDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
            editNationallity.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
            editSex.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
            editExporationDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
            editOptionalVal.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.txt_date_of_birth).setVisibility(View.GONE);
            editDateOfBirth.setVisibility(View.GONE);

            findViewById(R.id.txt_tipo_documento).setVisibility(View.GONE);
            editTipoDocumento.setVisibility(View.GONE);

            findViewById(R.id.label_mrz).setVisibility(View.GONE);
            editRawMrz.setVisibility(View.GONE);

            findViewById(R.id.label_sdk).setVisibility(View.GONE);
            editSDK.setVisibility(View.GONE);
        }
        if(MainActivity.getOcultarInfo() == true){
            findViewById(R.id.txt_date_of_birth).setVisibility(View.GONE);
            editDateOfBirth.setVisibility(View.GONE);
            findViewById(R.id.txt_given_name).setVisibility(View.GONE);
            editGivenName.setVisibility(View.GONE);
            findViewById(R.id.txt_surname).setVisibility(View.GONE);
            surName.setVisibility(View.GONE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.GONE);
            editIssuingCount.setVisibility(View.GONE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.GONE);
            editIssuingDate.setVisibility(View.GONE);
            findViewById(R.id.txt_nationality).setVisibility(View.GONE);
            editNationallity.setVisibility(View.GONE);
            findViewById(R.id.txt_sex).setVisibility(View.GONE);
            editSex.setVisibility(View.GONE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.GONE);
            editExporationDate.setVisibility(View.GONE);
            findViewById(R.id.txt_optional_values).setVisibility(View.GONE);
            editOptionalVal.setVisibility(View.GONE);
        }else{
            findViewById(R.id.txt_date_of_birth).setVisibility(View.VISIBLE);
            editDateOfBirth.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);
            editGivenName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
            surName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
            editIssuingCount.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
            editIssuingDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
            editNationallity.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
            editSex.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
            editExporationDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
            editOptionalVal.setVisibility(View.VISIBLE);
        }

        if (datos.has("QR_valido") && !datos.optBoolean("QR_valido")) {
            submitBtn.setEnabled(false);
            String textoError = "Fecha de caducidad del QR no válida:\n" + datos.optString("timestampQR");
            Toast.makeText(this, textoError, Toast.LENGTH_LONG).show();
            Snackbar snack = Snackbar.make(findViewById(R.id.scannedLayout), textoError, 10000); // 10 segundos
            snack.setAction("OK", v -> {});
            snack.setTextColor(Color.WHITE);
            snack.show();
        }else{
            submitBtn.setEnabled(true);
            Submit();
        }
    }

    public void mostrarFoto(String base64Foto) {
        if (base64Foto == null || base64Foto.isEmpty()) return;

        Bitmap bmp;

        try {
            // 1. Limpieza inicial
            String cleanBase64 = base64Foto.trim().replaceAll("\\s+", "");
            byte[] imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

            // 2. ¿Es un JP2 (del QR)?
            // El stream de JP2 suele empezar por 0x00 00 00 0C o FF 4F
            if (esJpeg2000(imageBytes)) {
                Log.d("FOTO", "Procesando como JP2 (Gemalto)");
                JP2Decoder decoder = new JP2Decoder(imageBytes);
                bmp = decoder.decode();
                imgMrz.setImageBitmap(bmp);
                imagenTipo = "QR";
                imgMrz.setVisibility(View.VISIBLE);
            }
            // 3. Es un JPG/PNG normal (de Barkoder)
            else {

                Log.d("FOTO", "Procesando como JPG estándar (Barkoder)");
                bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgMrz.setImageBitmap(bmp);
                imagenTipo = "MRZ";
                if (MainActivity.getDebugMode() == true){
                    imgMrz.setVisibility(View.VISIBLE);
                }else{
                    imgMrz.setVisibility(View.GONE);
                }
            }
            imagenMRZBase64 = convertirBitmapABase64Jpg(bmp);

        } catch (Exception e) {
            Log.e("FOTO", "Error decodificando imagen: " + e.getMessage());
        }
    }


    // Función auxiliar para detectar si es JPEG 2000
    private boolean esJpeg2000(byte[] bytes) {
        if (bytes.length < 4) return false;
        // Firmas típicas de JPEG 2000
        return (bytes[0] == (byte) 0x00 && bytes[1] == (byte) 0x00 && bytes[2] == (byte) 0x00 && bytes[3] == (byte) 0x0C) ||
                (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0x4F);
    }

    private String convertirBitmapABase64Jpg(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Comprimimos el bitmap a JPEG.
        // 90 es una calidad excelente, 100 es máximo.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Devolvemos el string listo para el servidor
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cleanEditText(); //Limpiamos los datos de los editText
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                JSONObject jsonObj = new JSONObject(data.getStringExtra("key"));
                String tipo = jsonObj.optString("type", "");

                if (tipo.equals("QR")) {
                    procesarLecturaQR();
                } else {
                    procesarLecturaMRZ(jsonObj);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onActivityResult: " + e.getMessage());
            }
        }
    }

    private void procesarLecturaQR() {
        new Thread(() -> {
            try {
                Log.i(TAG, "Procesando QR...");

                // 1. RECUPERAMOS los bytes del "túnel" (Holder)
                byte[] rawBytes = ScanDataHolder.rawBarcodeData;

                // 2. LIMPIAMOS EL HOLDER YA
                ScanDataHolder.rawBarcodeData = null;

                if (rawBytes != null) {
                    // 3. PARSEAMOS el binario puro para obtener los campos del DNI
                    // Esta es la función que creamos para entender el formato ASN.1/DER
                    JSONObject result = MiDniParser.parsear(rawBytes);

                    Log.d(TAG, "Resultado de Parsear MiDni: " + result);

                    // 4. ADAPTAMOS al formato estándar que entiende tu UI
                    //JSONObject jsonUnificado = new JSONObject();
                    JsonFinalUnificado = new JSONObject();

                    // Mapeamos lo que sale del Parser a los nombres de tu "contrato unificado"
                    JsonFinalUnificado.put("nombre", result.optString("first_name", ""));
                    JsonFinalUnificado.put("apellidos", result.optString("last_name", ""));
                    JsonFinalUnificado.put("dni", result.optString("document_number", ""));
                    if (result.has("issuing_country") && result.optString("issuing_country").equals("ES")){
                        JsonFinalUnificado.put("pais_emisor", "ESP");
                    }else{
                        JsonFinalUnificado.put("pais_emisor", result.optString("issuing_country", ""));
                    }
                    JsonFinalUnificado.put("nacionalidad", result.optString("nationality", ""));

                    // Normalizamos la fecha (DNIe suele dar DD-MM-AAAA)
                    String fechaNac = result.optString("date_of_birth", "");
                    if (fechaNac.equals("")){
                        JsonFinalUnificado.put("fecha_nacimiento", "");
                        JsonFinalUnificado.put("mayor_edad", result.optString("is_adult"));
                        if (result.optString("is_adult").equals("true")) {
                            JsonFinalUnificado.put("edad", "MAYOR DE EDAD");
                        }else{
                            JsonFinalUnificado.put("edad", "MENOR DE EDAD");
                        }
                    }else{
                        JsonFinalUnificado.put("fecha_nacimiento", fechaNac);
                        JsonFinalUnificado.put("edad", result.optString("age", ""));
                    }
                    JsonFinalUnificado.put("sexo", result.optString("gender", ""));

                    String fechaExpedicion = result.optString("date_of_issuance_estimated", "");
                    String fechaCaducidad = result.optString("date_of_expiry", "");
                    JsonFinalUnificado.put("fecha_caducidad", fechaCaducidad);

                    JsonFinalUnificado.put("valores_opciones", result.optString("optional_data", ""));

                    JsonFinalUnificado.put("tipo", "QR MiDNI");
                    JsonFinalUnificado.put("tipo_verificacion", result.optString("tipo_verificacion_label", ""));
                    JsonFinalUnificado.put("QR_valido", result.optBoolean("QR_valido"));
                    JsonFinalUnificado.put("timestampQR", result.optString("timestampQR"));


                    ///RAW
                    StringBuilder sb = new StringBuilder();
                    // Definimos cuántos bytes queremos ver (ej: 32)
                    int bytesAVer = Math.min(rawBytes.length, 32);

                    for (int j = 0; j < bytesAVer; j++) {
                        sb.append(String.format("%02x ", rawBytes[j] & 0xFF));
                        // Opcional: Añadir un guion cada 8 bytes para que sea más legible
                        if ((j + 1) % 8 == 0 && j != bytesAVer - 1) sb.append("- ");
                    }
                    JsonFinalUnificado.put("raw", sb.toString());
                    //Log.d("BARKODER_FINAL", "Buffer (L:" + rawBytes.length + "): " + sb.toString());


                    // La foto que el parser guardó como "image_mrz" para ser compatible
                    JsonFinalUnificado.put("foto", result.optString("image_mrz", ""));

                    // 4. VOLCAMOS a la UI en el hilo principal
                    runOnUiThread(() -> {
                        try {
                            // Log para verificar qué estamos enviando a la pantalla
                            Log.i(TAG, "Enviando datos unificados a la UI: " + JsonFinalUnificado.toString());

                            addResultToEditText(JsonFinalUnificado);

                        } catch (JSONException e) {
                            // Este catch es vital para que compile y para capturar errores de formato de último minuto
                            Log.e(TAG, "Error de JSON al volcar a la UI: " + e.getMessage());
                            Toast.makeText(this, "Error visualizando los datos", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Log.e(TAG, "Error: ScanDataHolder.rawBarcodeData es nulo");
                }
            } catch (Exception e) {
                Log.e(TAG, "Fallo crítico procesando QR: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al procesar el código QR", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }



    private void procesarLecturaMRZ(JSONObject mrzRaw) {
        Log.i(TAG, "Procesando MRZ: " + mrzRaw);
        try {
            JSONObject details = mrzRaw.getJSONObject("details");
            JSONObject inner = new JSONObject(details.getString("JSON"));
            JSONObject normal = inner.getJSONObject("normal");
            JSONObject formatted = inner.getJSONObject("formatted");

            //JSONObject jsonUnificado = new JSONObject();
            JsonFinalUnificado = new JSONObject();
            JsonFinalUnificado.put("nombre", normal.optString("first_name", ""));
            JsonFinalUnificado.put("apellidos", normal.optString("last_name", ""));

            // Lógica de Swap DNI/Soporte que ya tenías
            String nroDoc = normal.optString("document_number", "");
            String optData = normal.optString("optional_data", "");
            if (!optData.isEmpty()) {
                JsonFinalUnificado.put("dni", optData); // El DNI real suele ir aquí en MRZ
                JsonFinalUnificado.put("valores_opcionales", nroDoc);
            } else {
                JsonFinalUnificado.put("dni", nroDoc);
            }

            JsonFinalUnificado.put("pais_emisor", normal.optString("issuing_country", ""));
            JsonFinalUnificado.put("nacionalidad", normal.optString("nationality", ""));
            JsonFinalUnificado.put("fecha_nacimiento", formatted.optString("date_of_birth", ""));
            JsonFinalUnificado.put("edad", calcularEdad(formatted.optString("date_of_birth", "")));
            JsonFinalUnificado.put("sexo", normal.optString("gender", ""));
            JsonFinalUnificado.put("fecha_expedicion", normal.optString("date_of_issuance_estimated", ""));
            JsonFinalUnificado.put("fecha_caducidad", formatted.optString("date_of_expiry", ""));


            JsonFinalUnificado.put("tipo", normal.optString("document_type", "MRZ"));
            JsonFinalUnificado.put("tipo_verificacion", "MRZ");
            JsonFinalUnificado.put("raw", inner.optString("raw", ""));
            JsonFinalUnificado.put("foto", mrzRaw.optString("image_mrz", ""));

            addResultToEditText(JsonFinalUnificado);
        } catch (Exception e) {
            Log.e(TAG, "Error procesando MRZ: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == scanBtn) {
            startScanner();
        }

        if (view==manualBtn){
            MainActivity.solicitar_dni(this, findViewById(R.id.scannedLayout));
        }
//        if (view==imgBtnFront){
//            REQUEST_IMAGE_SCAN=2;
//            startImageScan();
//        }
//        if ( view==imgBtnBack)
//        {
//            REQUEST_IMAGE_SCAN=3;
//            startImageScan();
//        }
        if (view==backBtn){
            //MainActivity.setScannerInicial(false);
            finish();
        }
        if (view==submitBtn){
            //startActivity(new Intent(this,ProceedToCheckInActivity.class));
            Submit();
        }

        if (view == datosCliente){
            clicks++;
            if(clicks >= 10) {
                MainActivity.setDebugMode(true);

                findViewById(R.id.txt_date_of_birth).setVisibility(View.VISIBLE);
                editDateOfBirth.setVisibility(View.VISIBLE);

                findViewById(R.id.txt_tipo_documento).setVisibility(View.VISIBLE);
                editTipoDocumento.setVisibility(View.VISIBLE);

                findViewById(R.id.label_mrz).setVisibility(View.VISIBLE);
                editRawMrz.setVisibility(View.VISIBLE);

//                findViewById(R.id.label_sdk).setVisibility(View.VISIBLE);
//                editSDK.setVisibility(View.VISIBLE);

                findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);
                editGivenName.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
                surName.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
                editIssuingCount.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
                editIssuingDate.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
                editNationallity.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
                editSex.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
                editExporationDate.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
                editOptionalVal.setVisibility(View.VISIBLE);

                imgMrz.setVisibility(View.VISIBLE);
            }
        }
    }
//
//    private void startImageScan() {
//        Intent intent = new Intent(getApplicationContext(),ScannerActivity.class);
//        intent.putExtra("imagekey",REQUEST_IMAGE_SCAN);
//        startActivity(intent);
//    }

    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String reg = "^[a-zA-Z0-9]*$";
            if(!source.toString().matches(reg)) {
                return "";
            }

            return null;
        }
    };

    private void startScanner() {
        startActivityForResult(new Intent(getApplicationContext(),BarkoderActivity.class),REQUEST_CODE);
    }

    private void Submit(){
        //Bloqueamos el botón
        submitBtn.setEnabled(false);

        //URL of the request we are sending
        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrl_mrz_barkoder(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Context context = getApplicationContext();

                        Log.d(TAG, "Respuesta del Servidor: \n"+ response);

                        // 1. LIMPIEZA Y VALIDACIÓN INICIAL
                        if (response == null || response.trim().isEmpty()) {
                            Toast.makeText(context, "Error: El servidor respondió vacío", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"Error: El servidor respondió vacío");
                            return;
                        }

                        // 2. FILTRO ANTI-HTML (Evita el crash por <br />)
                        String cleanResponse = response.trim();
                        if (!cleanResponse.startsWith("{")) {
                            // Si no empieza por {, es probable que sea un error PHP (HTML)
                            Log.e("SERVER_ERROR", "Respuesta no válida del servidor: " + response);

                            if(MainActivity.getDebugMode()) {
                                Snackbar.make(findViewById(R.id.scannedLayout),
                                        "ERROR SERVIDOR (No es JSON):\n\n" + response,
                                        Snackbar.LENGTH_INDEFINITE).show();
                            } else {
                                Toast.makeText(context, "Error de Respuesta del Servidor. Contacte con Soporte.", Toast.LENGTH_LONG).show();
                            }
                            return; // Salimos antes de intentar parsear
                        }



                        boolean permitido=false, resultado_correcto=false;
                        //boolean certificadoCovid = false; //Certificado Covid
                        boolean enMiLista = false; //Está en la lista personal
                        String mensaje="";
                        String mensajeMiLista="";
                        boolean solicitarJugador = false; //Encuesta si el cliente es Jugador
                        //int numAccesosHoy=-1;
                        boolean imprimirPromocion=false;

                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        String msgErrorExcepcion="";
                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                resultado_correcto=jsonObject.getBoolean("resultado_correcto");
                                permitido=jsonObject.getBoolean("acceso_permitido");
                                mensaje=jsonObject.getString("mensaje");
                                //certificadoCovid = jsonObject.getBoolean("certificadoCovid"); //Certificado Covid
                                enMiLista = jsonObject.getBoolean("enMiLista"); //Está en la lista personal
                                mensajeMiLista = jsonObject.getString("mensajeMiLista");

                                if ( MainActivity.getVersion().equals("SerOnuba")) {
                                    solicitarJugador = jsonObject.getBoolean("solicitarJugador");
                                    imprimirPromocion = jsonObject.getBoolean("promocion");
                                }
                            }else{
                                if(jsonObject.getString("status").equals("true")) {
                                    resultado_correcto=false;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(getApplicationContext(), "Error procesando JSON", Toast.LENGTH_LONG).show();

                            if(MainActivity.getDebugMode() == true) {
                                msgErrorExcepcion=e.toString();
                            }
                        }


                        if(MainActivity.getDebugMode() == true) {

                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar;
                            if (msgErrorExcepcion.equals("")) {
                                mySnackbar = Snackbar.make(findViewById(R.id.scannedLayout), "JSON Procesado:\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }else{
                                mySnackbar = Snackbar.make(findViewById(R.id.scannedLayout), "Error Procesando JSON:\n\n" + msgErrorExcepcion + "\n\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }
//                            mySnackbar.setAction("Ver", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            final AlertDialog.Builder alertDebug = new AlertDialog.Builder(ScannedBarkoderActivity.this, R.style.MyDialogThemeNeutral );
//
//                                            alertDebug.setTitle("Repuesta del Servidor (Debug):");
//                                            alertDebug.setIcon(R.drawable.ic_baseline_info_24);
//                                            alertDebug.setMessage(response);
//                                            alertDebug.setCancelable(false);
//
//                                            alertDebug.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//
//                                            AlertDialog dialog = alertDebug.show();
//
//                                            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                                            messageText.setTextSize(18);
//                                            messageText.setGravity(Gravity.CENTER);
//                                            dialog.show();
//                                            ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
//                                        }
//                                    });

                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }


                        int estilo = R.style.MyDialogThemeProhibido;

                        String titulo;
                        int icono;
                        if(resultado_correcto == true && permitido == true) {
                            estilo = R.style.MyDialogThemePermitido;
                            titulo="Accceso Permitido";
                            icono=R.drawable.ic_baseline_check_circle_24;
                            if( MainActivity.getImprimirVales() == true && imprimirPromocion == true ) {
                                MainActivity.imprimir(editDocNum.getText().toString(), ScannedBarkoderActivity.this, findViewById(R.id.scannedLayout));
                                titulo="Accceso Permitido + PROMO";
                                mensaje=mensaje+"<br><br> IMPRIMIENDO PROMO";
                            }

                            //Tono de Aviso
                            if (enMiLista == true){
                                MainActivity.tonoAviso(context, 2); //Aviso Largo
                            }
                        }else{
                            if (resultado_correcto == true && permitido == false) {
                                estilo = R.style.MyDialogThemeProhibido;
                                titulo="Acceso Denegado";
                                icono=R.drawable.ic_baseline_not_interested_24;

                                MainActivity.tonoAviso(context, 2); //Aviso Largo
                            }else{ //el Resultado no es correcto
                                estilo = R.style.MyDialogThemeNeutral;
                                titulo="Resultado";
                                icono=R.drawable.ic_baseline_info_24;

                                MainActivity.tonoAviso(context, 0); //Aviso Corto
                            }


                        }


                        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(ScannedBarkoderActivity.this, estilo );

                        alertOpciones.setTitle(titulo);
                        alertOpciones.setIcon(icono);
                        alertOpciones.setMessage(Html.fromHtml(mensaje));
                        alertOpciones.setCancelable(false);

                        if (enMiLista == true) {
                            LinearLayout diagLayout = new LinearLayout(ScannedBarkoderActivity.this);
                            diagLayout.setOrientation(LinearLayout.VERTICAL);
                            TextView textoMensaje = new TextView(ScannedBarkoderActivity.this);
                            textoMensaje.setTextColor(Color.BLACK);
                            textoMensaje.setText(Html.fromHtml(mensajeMiLista));
                            textoMensaje.setPadding(30, 30, 10, 30);
                            textoMensaje.setBackgroundColor(ContextCompat.getColor(ScannedBarkoderActivity.this, R.color.peligro));
                            textoMensaje.setGravity(Gravity.CENTER);
                            textoMensaje.setTextSize(30);
                            diagLayout.addView(textoMensaje);
                            alertOpciones.setView(diagLayout);
                        }

                        alertOpciones.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        alertOpciones.setNeutralButton("LEER DE NUEVO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startScanner();
                            }
                        });
/*
                        if(certificadoCovid == false) {
                            alertOpciones.setNegativeButton("Certificar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i;
                                    PackageManager manager = getPackageManager();
                                    try {
                                        i = manager.getLaunchIntentForPackage("ch.admin.bag.covidcertificate.verifier");
                                        if (i == null)
                                            throw new PackageManager.NameNotFoundException();
                                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                                        startActivity(i);
                                    } catch (PackageManager.NameNotFoundException e) {

                                    }
                                    alertOpciones.show();
                                    ((TextView) alertOpciones.show().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                            });
                        }
*/
                        AlertDialog dialog = alertOpciones.show();

                        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                        messageText.setTextSize(18);
                        messageText.setGravity(Gravity.CENTER);
                        dialog.show();
                        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

                        //Alert opciones Jugador 30/08/22
                        if ( MainActivity.getVersion().equals("SerOnuba") && resultado_correcto == true && permitido == true && solicitarJugador == true) {
                            final CharSequence[] OPCIONES_ALERTA = {"Jugador Fuerte", "Jugador Frecuente", "Jugador Ocasional", "No Juega", "No lo sé"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(ScannedBarkoderActivity.this, R.style.Theme_AppCompat_Dialog);
                            //builder.setTitle(titulo);
                            builder.setIcon(R.drawable.ic_baseline_question_answer_24);
                            //builder.setMessage("Seleccione una de las siguientes opciones:");
                            builder.setCancelable(false);
                            builder.setTitle("¿El Cliente es Jugador de Máquinas?");
                            builder.setItems(OPCIONES_ALERTA, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // which representa el índice del arreglo de opciones
                                    String eleccion = OPCIONES_ALERTA[which].toString();
                                    // Aquí puedes mostrar la elección o hacer lo que sea con ella.
                                    //Toast.makeText(ScannedBarkoderActivity.this, "Elegiste: " + eleccion + ' ' + MainActivity.getVersion(), Toast.LENGTH_SHORT).show();
                                    MainActivity.guardarJugador(getApplicationContext(), editDocNum.getText().toString(), eleccion);
                                    // En el click se descarta el diálogo
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                        //Fin Alert opciones 30/08/22

                        //Custom AlertDialog DESACTIVADO
                        /*
                        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                        ViewGroup viewGroup = findViewById(android.R.id.content);

                        //then we will inflate the custom alert dialog xml that we created
                        final View dialogView = LayoutInflater.from(context).inflate(R.layout.my_dialog_scanned_ok, viewGroup, false);

                        //Modificamos el mensaje
                        TextView textView = (TextView) dialogView.findViewById(R.id.mensaje);
                        textView.setText(mensaje);

                        //Now we need an AlertDialog.Builder object
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ScannedBarkoderActivity.this);

                        //setting the view of the builder to our custom view that we already inflated
                        builder.setView(dialogView);

                        //finally creating the alert dialog and displaying it
                        final AlertDialog alertDialog = builder.create();

                        Button ok_item = (Button)dialogView.findViewById(R.id.ok_btn);
                        ok_item.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                alertDialog.dismiss();

                            }
                        });
                        Button scanear_item = (Button)dialogView.findViewById(R.id.escanear_btn);
                        scanear_item.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                startScanner();

                            }
                        });
                        alertDialog.show();

                         */
                        //Fin de Custom AlertDialog









///////////////////////////////
                        submitBtn.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "Compruebe su Conexión a Internet:\r\n\r\n" + error.toString();

                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        submitBtn.setEnabled(true);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Log.d(TAG, "Tengo para usar el JsonFinalUnificado: " + JsonFinalUnificado);

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                // 1. Datos obligatorios del sistema
                params.put( "dispositivo", MainActivity.getIdentificadorAndroid());
                params.put( "usuario", MainActivity.getUsuario());
                params.put( "provincia", MainActivity.getProvincia());
                params.put( "version", MainActivity.getVersion());

                params.put( "dni", editDocNum.getText().toString());


                // 2. Datos del documento (Solo si tienen contenido)
                putIfNotEmpty(params,"fecha_nacimiento", editDateOfBirth.getText().toString());
                putIfNotEmpty(params, "nombre", editGivenName.getText().toString());
                putIfNotEmpty(params,"apellidos", surName.getText().toString());
                putIfNotEmpty(params,"sexo", editSex.getText().toString());
                putIfNotEmpty(params,"nacionalidad", editNationallity.getText().toString());
                putIfNotEmpty(params,"pais_origen", editIssuingCount.getText().toString());
                putIfNotEmpty(params,"tipo_documento", editTipoDocumento.getText().toString());
                putIfNotEmpty(params,"tipo_verificacion", JsonFinalUnificado.optString("tipo_verificacion"));
                putIfNotEmpty(params,"fecha_expedicion", editIssuingDate.getText().toString());
                putIfNotEmpty(params,"caducidad_dni", editExporationDate.getText().toString());
                putIfNotEmpty(params,"opcional", editOptionalVal.getText().toString());

                if (JsonFinalUnificado.has("mayor_edad")){
                    params.put("mayor_edad", JsonFinalUnificado.optString("mayor_edad"));
                }


                params.put( "mrz_completo", editRawMrz.getText().toString());

                Log.d(TAG, "Enviando al Servidor:\n" + params.toString());

                // 3. Imágenes
                params.put("imagenTipo", imagenTipo);
                params.put("imagenMRZBase64", imagenMRZBase64);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
    // Método auxiliar para mantener el código limpio
    private void putIfNotEmpty(Map<String, String> params, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value.trim());
        }
    }

    private int calcularEdad(String fechaNacimiento) {
        if (fechaNacimiento == null) return -1;

        // Quitamos guiones, barras o espacios: "1978-08-06" -> "19780806"
        String limpia = fechaNacimiento.replaceAll("[^0-9]", "");
        if (limpia.length() < 8) return -1;

        try {
            int anio, mes, dia;

            anio = Integer.parseInt(limpia.substring(0, 4)); //1978
            mes = Integer.parseInt(limpia.substring(4, 6)); //08
            dia = Integer.parseInt(limpia.substring(6, 8)); //06

            java.util.Calendar nac = java.util.Calendar.getInstance();
            nac.set(anio, mes - 1, dia);

            java.util.Calendar hoy = java.util.Calendar.getInstance();

            int edad = hoy.get(java.util.Calendar.YEAR) - nac.get(java.util.Calendar.YEAR);

            // Ajuste de precisión (si aún no ha llegado su cumple este año)
            if (hoy.get(java.util.Calendar.MONTH) < nac.get(java.util.Calendar.MONTH) ||
                    (hoy.get(java.util.Calendar.MONTH) == nac.get(java.util.Calendar.MONTH) &&
                            hoy.get(java.util.Calendar.DAY_OF_MONTH) < nac.get(java.util.Calendar.DAY_OF_MONTH))) {
                edad--;
            }

            return edad;
        } catch (Exception e) {
            Log.e("EDAD", "Error al calcular con fecha: " + fechaNacimiento);
            return -1;
        }
    }

} //Fin de la clase ScannedBarkoderActivity