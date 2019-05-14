Feature: Client Feature

  Scenario: Client with address
    Given los datos entrada obtenidos de "./data/clientes_with_address.csv" con la estructura "clientes_estructura.properties"
     When se llame al endpoint "/client/by-id" por el metodo "GET"
      And enviando los datos "idClient"
     Then el codigo http del endpoint es "200"
      And el codigo de la respuesta del endpoint es "0"
      And la respuesta cumple la especificacion "./schemas/client_with_address.json"


  Scenario: Client without address
    Given los datos entrada obtenidos de "./data/clientes_without_address.csv" con la estructura "clientes_estructura.properties"
    When se llame al endpoint "/client/by-id" por el metodo "GET"
    And enviando los datos "idClient"
    Then el codigo http del endpoint es "200"
    And el codigo de la respuesta del endpoint es "0"
    And la respuesta cumple la especificacion "./schemas/client_without_address.json"

