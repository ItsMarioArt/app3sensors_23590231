package com.example.app3sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Sensor lightSensor;
    private Sensor accelerometerSensor; // Reemplazado sensor de orientación por acelerómetro

    private TextView stepCountTextView;
    private TextView lightValueTextView;
    private TextView accelerometerTextView; // Cambio de nombre para reflejar el nuevo sensor

    private int stepCount = 0;

    private static final int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar los TextView
        stepCountTextView = findViewById(R.id.stepCountTextView);
        lightValueTextView = findViewById(R.id.lightValueTextView);
        accelerometerTextView = findViewById(R.id.orientationTextView); // Sigue usando el mismo ID del XML

        // Inicializar el SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Inicializar los sensores
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Verificar si el dispositivo tiene los sensores requeridos
        if (stepSensor == null) {
            Toast.makeText(this, "Este dispositivo no tiene sensor de pasos", Toast.LENGTH_SHORT).show();
        }

        if (lightSensor == null) {
            Toast.makeText(this, "Este dispositivo no tiene sensor de luz", Toast.LENGTH_SHORT).show();
        }

        if (accelerometerSensor == null) {
            Toast.makeText(this, "Este dispositivo no tiene acelerómetro", Toast.LENGTH_SHORT).show();
        }

        // Establecer texto inicial para el acelerómetro
        accelerometerTextView.setText("Acelerómetro:\nX: 0.00 m/s²\nY: 0.00 m/s²\nZ: 0.00 m/s²\n\n¡Mueve el dispositivo para ver cambios!");

        // Verificar y solicitar permisos
        checkPermissions();
    }

    private void checkPermissions() {
        // Verificar permisos para Android 10 (API 29) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        ACTIVITY_RECOGNITION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, reiniciar el sensor de pasos
                if (stepSensor != null) {
                    sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            } else {
                // Permiso denegado
                Toast.makeText(this, "Se requiere permiso para contar pasos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registrar los sensores cuando la aplicación esté activa
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Desregistrar los sensores cuando la aplicación no esté activa
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Determinar qué sensor ha enviado el evento
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                // Si es la primera medición, inicializar el contador
                if (stepCount == 0) {
                    stepCount = (int) event.values[0];
                }

                // Mostrar la cantidad de pasos (diferencia entre el valor actual y el inicial)
                int currentSteps = (int) event.values[0] - stepCount;
                stepCountTextView.setText("Pasos: " + currentSteps);
                break;

            case Sensor.TYPE_LIGHT:
                // Mostrar el valor de la luz ambiental en lux
                float lightValue = event.values[0];
                lightValueTextView.setText("Nivel de luz: " + lightValue + " lx");
                break;

            case Sensor.TYPE_ACCELEROMETER:
                // Mostrar los valores del acelerómetro
                float x = event.values[0]; // Aceleración en el eje X (m/s²)
                float y = event.values[1]; // Aceleración en el eje Y (m/s²)
                float z = event.values[2]; // Aceleración en el eje Z (m/s²)

                accelerometerTextView.setText("Acelerómetro:\n" +
                        "X: " + String.format("%.2f", x) + " m/s²\n" +
                        "Y: " + String.format("%.2f", y) + " m/s²\n" +
                        "Z: " + String.format("%.2f", z) + " m/s²\n\n" +
                        "¡Mueve el dispositivo para ver cambios!");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos implementar nada aquí para esta aplicación
    }
}