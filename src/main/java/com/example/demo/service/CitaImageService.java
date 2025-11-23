package com.example.demo.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class CitaImageService {

    public byte[] generarImagenCita(String nombrePaciente, String nombreMedico, 
                                     LocalDate fecha, LocalTime hora, String motivo) throws IOException {
        
        // Crear imagen de 800x600
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Mejorar calidad de renderizado
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo degradado
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 123, 200), 0, height, new Color(0, 200, 255));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Rectángulo blanco central
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(50, 50, width - 100, height - 100, 30, 30);
        
        // Borde del rectángulo
        g2d.setColor(new Color(0, 123, 200));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(50, 50, width - 100, height - 100, 30, 30);
        
        // Configurar fuentes
        Font titleFont = new Font("Arial", Font.BOLD, 36);
        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font valueFont = new Font("Arial", Font.PLAIN, 18);
        
        // Título
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(titleFont);
        String title = "Confirmación de Cita Médica";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 120);
        
        // Línea separadora
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(100, 150, width - 100, 150);
        
        // Información de la cita
        int yPosition = 200;
        int labelX = 120;
        int valueX = 350;
        int lineSpacing = 60;
        
        // Formatear fecha y hora
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // Paciente
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(labelFont);
        g2d.drawString("Paciente:", labelX, yPosition);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(valueFont);
        g2d.drawString(nombrePaciente, valueX, yPosition);
        
        // Médico
        yPosition += lineSpacing;
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(labelFont);
        g2d.drawString("Médico:", labelX, yPosition);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(valueFont);
        g2d.drawString(nombreMedico, valueX, yPosition);
        
        // Fecha
        yPosition += lineSpacing;
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(labelFont);
        g2d.drawString("Fecha:", labelX, yPosition);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(valueFont);
        g2d.drawString(fecha.format(dateFormatter), valueX, yPosition);
        
        // Hora
        yPosition += lineSpacing;
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(labelFont);
        g2d.drawString("Hora:", labelX, yPosition);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(valueFont);
        g2d.drawString(hora.format(timeFormatter), valueX, yPosition);
        
        // Motivo
        yPosition += lineSpacing;
        g2d.setColor(new Color(0, 123, 200));
        g2d.setFont(labelFont);
        g2d.drawString("Motivo:", labelX, yPosition);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(valueFont);
        
        // Dividir motivo en múltiples líneas si es muy largo
        String motivoTexto = motivo != null ? motivo : "Consulta general";
        if (motivoTexto.length() > 50) {
            String[] palabras = motivoTexto.split(" ");
            StringBuilder linea = new StringBuilder();
            int lineY = yPosition;
            
            for (String palabra : palabras) {
                if (linea.length() + palabra.length() + 1 > 50) {
                    g2d.drawString(linea.toString(), valueX, lineY);
                    lineY += 25;
                    linea = new StringBuilder(palabra + " ");
                } else {
                    linea.append(palabra).append(" ");
                }
            }
            if (linea.length() > 0) {
                g2d.drawString(linea.toString(), valueX, lineY);
            }
        } else {
            g2d.drawString(motivoTexto, valueX, yPosition);
        }
        
        // Pie de página
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        String footer = "Por favor, llegue 10 minutos antes de su cita";
        fm = g2d.getFontMetrics();
        int footerX = (width - fm.stringWidth(footer)) / 2;
        g2d.drawString(footer, footerX, height - 80);
        
        g2d.dispose();
        
        // Convertir a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
