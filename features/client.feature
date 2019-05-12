Feature: Client Feature

  Scenario: Prestamos con garantias y avales
    Given los datos entrada obtenidos de "./data/clientes.csv" con la estructura "clientes_estructura.properties"
     When se llame al endpoint "/client/by-id" por el metodo "GET"
      And enviando los datos "idcliente"
     Then el codigo http del endpoint es "200"
      And el codigo de la respuesta del endpoint es "0"
#      And la respuesta cumple la especificacion "prestamos_con_garantias_y_avales_schema.json"


# Scenario: Prestamos con garantias y avales
# Given el endpoint "/test-endpoint"
# And los datos entrada obtenidos de "prestamos.csv"
# When se llame al endpoint por el metodo "GET"
# And enviando los datos "codigo_prestamo, periodo"
# Then el codigo http del endpoint es "200"
# And el codigo de la respuesta del endpoint es "0"
# "prestamos_con_garantias_y_avales_schema.json"