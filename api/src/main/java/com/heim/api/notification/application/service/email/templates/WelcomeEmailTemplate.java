package com.heim.api.notification.application.service.email.templates;

public class WelcomeEmailTemplate {
    public static String build(String firstName) {

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
                                          <h1 style='color: #ffffff; font-size: 28px; font-weight: 800; margin: 0;'>¡Hola, %s tu mudanza acaba de volverse más simple!</h1>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding: 0 40px 30px 40px; text-align: center; color: #D1D5DB;'>
                                          <p style='font-size: 18px; line-height: 1.6; margin: 0;'>
                                           Gracias por registrarte en Heim.
                
                                           Sabemos que encontrar transporte para una mercancía o un despacho urgente puede tomar más tiempo del que debería. Llamadas, mensajes, cotizaciones que cambian y conductores que no siempre están disponibles.
                
                                           Por eso creamos Heim.
                
                                           Nuestra misión es ayudarte a encontrar transporte para tus envíos de forma más simple, con precios claros desde el inicio y sin perder tiempo buscando quién puede realizar el servicio.
                
                                           Con Heim puedes:
                
                                           ✅ Consultar el precio estimado de tu transporte antes de solicitarlo.
                
                                           ✅ Solicitar un vehículo desde tu celular.
                
                                           ✅ Conectar con conductores registrados en la plataforma.
                
                                           ✅ Hacer seguimiento a tu servicio desde la aplicación.
                
                                           Te invitamos a explorar la aplicación y realizar una cotización cuando necesites mover mercancía, hacer un despacho o transportar una carga.
                
                                           Estamos construyendo Heim paso a paso y tu participación es muy importante para nosotros.
                
                                           Gracias por confiar en este proyecto.
                
                                          </p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td align='center' style='padding-bottom: 40px;'>
                                          <a href='https://play.google.com/store/apps/details?id=com.heim.app.co&pcampaignid=web_share' style='background-color: #ffbc11; color: #000000; padding: 16px 32px; text-decoration: none; border-radius: 16px; font-weight: bold; font-size: 18px; display: inline-block; border-bottom: 4px solid #cc9a00;'>
                                            Abrir Heim
                                          </a>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding: 20px; background-color: #111111; text-align: center; color: #9CA3AF; font-size: 14px; border-top: 2px solid #ffbc11;'>
                                          <p style='margin: 0;'>Estamos contigo en cada kilómetro —  equipo de Heim</p>
                                        </td>
                                      </tr>
                                    </table>
                                    <p style='margin-top: 20px; color: #9CA3AF; font-size: 12px;'>© 2026 Heim Project. Todos los derechos reservados.</p>
                                  </td>
                                </tr>
                              </table>
                            </body>
                            </html>
                    """.formatted(firstName);

    }
}
