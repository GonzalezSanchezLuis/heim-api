package com.heim.api.notification.application.service.email.templates;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CancelledMoveEmailTemplate {

    public static String build(String firstName, String origin, String destination, LocalDateTime scheduledTime) {
        String formattedDate = scheduledTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' hh:mm a"));

        return """
                <!DOCTYPE html>
                <html>
                <body style='margin: 0; padding: 0; background-color: #000000; font-family: "Segoe UI", sans-serif;'>
                  <table width='100%%' border='0' cellspacing='0' cellpadding='0'>
                    <tr>
                      <td align='center' style='padding: 40px 0;'>
                        <table width='500' border='0' cellspacing='0' cellpadding='0' style='background-color: #000000; border: 2px solid #ffbc11; border-radius: 24px; overflow: hidden;'>
                          <tr>
                            <td align='center' style='padding: 40px 40px 20px 40px;'>
                              <h1 style='color: #ffffff; font-size: 26px; font-weight: 800; margin: 0;'>❌ Viaje cancelado, %s</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style='padding: 0 40px 30px 40px; color: #D1D5DB;'>
                              <p style='font-size: 16px; line-height: 1.8; margin: 0;'>
                                Queremos ser transparentes contigo. Hoy no pudimos encontrar un conductor para un viaje agendado y tuvimos que cancelarlo. Sabemos que esto no es ideal, y estamos trabajando para reclutar más conductores en tu zona.
                
                                Te agradecemos tu paciencia mientras crecemos. Si necesitas un viaje, te recomendamos solicitar con mayor anticipación o usar la opción inmediata.
                
                                Gracias por ser parte de este proceso.
                              </p>
                              <table width='100%%' style='margin-top: 20px; border-collapse: collapse;'>
                                <tr>
                                  <td style='padding: 10px; background-color: #111111; border-radius: 8px; color: #ffbc11; font-weight: bold;'>📍 Origen</td>
                                  <td style='padding: 10px; background-color: #111111; color: #ffffff;'>%s</td>
                                </tr>
                                <tr>
                                  <td style='padding: 10px; color: #ffbc11; font-weight: bold;'>🏁 Destino</td>
                                  <td style='padding: 10px; color: #ffffff;'>%s</td>
                                </tr>
                                <tr>
                                  <td style='padding: 10px; background-color: #111111; border-radius: 8px; color: #ffbc11; font-weight: bold;'>🕐 Fecha programada</td>
                                  <td style='padding: 10px; background-color: #111111; color: #ffffff;'>%s</td>
                                </tr>
                              </table>
                              <p style='font-size: 14px; color: #9CA3AF; margin-top: 20px;'>
                                Si necesitas programar un nuevo servicio, puedes hacerlo desde la aplicación en cualquier momento.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style='padding: 20px; background-color: #111111; text-align: center; color: #9CA3AF; font-size: 14px; border-top: 2px solid #ffbc11;'>
                              <p style='margin: 0;'>Estamos contigo en cada kilómetro — equipo de Heim</p>
                            </td>
                          </tr>
                        </table>
                        <p style='margin-top: 20px; color: #9CA3AF; font-size: 12px;'>© 2026 Heim Project. Todos los derechos reservados.</p>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(firstName, origin, destination, formattedDate);
    }
}
