package com.heim.api.notification.application.service.email.templates;

public class WelcomeEmailTemplate {
    public  static String build(String firstName,String appUrl,String brandColor){

        return """
                <!DOCTYPE html>
                            <html>
                            <body style='margin: 0; padding: 0; background-color: #F0F2F5; font-family: "Segoe UI", sans-serif;'>
                              <table width='100%%' border='0' cellspacing='0' cellpadding='0'>
                                <tr>
                                  <td align='center' style='padding: 40px 0;'>
                                    <table width='500' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border: 2px solid #E5E7EB; border-radius: 24px; overflow: hidden;'>
                                      <tr>
                                        <td align='center' style='padding: 40px 40px 20px 40px;'>
                                          <h1 style='color: #111827; font-size: 28px; font-weight: 800; margin: 0;'>¡Hola, %s tu mudanza acaba de volverse más simple!</h1>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding: 0 40px 30px 40px; text-align: center; color: #4B5563;'>
                                          <p style='font-size: 18px; line-height: 1.6; margin: 0;'>
                                           Mudarse no es solo mover cajas.
                                           Es cerrar ciclos, empezar algo nuevo y tomar decisiones importantes.
                
                                           En Heim estamos aquí para que ese proceso sea más fácil, más claro y sin sorpresas.
                
                                           Cuando estés listo, da el siguiente paso y comienza a planear tu mudanza con tranquilidad.
                                          </p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td align='center' style='padding-bottom: 40px;'>
                                          <a href='%s' style='background-color: %s; color: #ffffff; padding: 16px 32px; text-decoration: none; border-radius: 16px; font-weight: bold; font-size: 18px; display: inline-block; border-bottom: 4px solid #3730A3;'>
                                            Comenzar mi camino
                                          </a>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding: 20px; background-color: #F9FAFB; text-align: center; color: #9CA3AF; font-size: 14px; border-top: 2px solid #E5E7EB;'>
                                          <p style='margin: 0;'>Estamos contigo en cada kilómetro — el equipo de Heim</p>
                                        </td>
                                      </tr>
                                    </table>
                                    <p style='margin-top: 20px; color: #9CA3AF; font-size: 12px;'>© 2026 Heim Project. Todos los derechos reservados.</p>
                                  </td>
                                </tr>
                              </table>
                            </body>
                            </html>
                    """.formatted(firstName,brandColor,appUrl);

    }
}
