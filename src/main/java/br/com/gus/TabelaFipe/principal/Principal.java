package br.com.gus.TabelaFipe.principal;

import br.com.gus.TabelaFipe.model.Dados;
import br.com.gus.TabelaFipe.model.Modelos;
import br.com.gus.TabelaFipe.model.Veiculo;
import br.com.gus.TabelaFipe.service.ConsumoApi;
import br.com.gus.TabelaFipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
  private Scanner leitura = new Scanner(System.in);
  private ConsumoApi consumo = new ConsumoApi();
  private ConverteDados conversor = new ConverteDados();
  private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1";

  public void exibeMenu() {
    var menu = """
        *** OPÇÔES ***
        Carro
        Moto
        Caminhão
        
        Digite uma das opções:
        """;

    System.out.println(menu);
    var opcao = leitura.nextLine();
    String endereco = "";

    if (opcao.toLowerCase().contains("car")){
      endereco = URL_BASE + "/carros/marcas";
    } else if (opcao.toLowerCase().contains("moto")){
      endereco = URL_BASE + "/motos/marcas";
    } else if (opcao.toLowerCase().contains("caminh")){
      endereco = URL_BASE + "/caminhoes/marcas";
    }
    var json = consumo.obterDados(endereco);
    System.out.println(json);
    var marcas = conversor.obterLista(json, Dados.class);

    System.out.println("\nMarcas do veiculo escolhido: ");
    marcas.stream()
        .sorted(Comparator.comparing(Dados::nome))
        .forEach(System.out::println);

    System.out.println("Informe o código da marca para consultar:");
    var codigoMarca = leitura.nextLine();

    endereco += "/" + codigoMarca + "/modelos";
    json = consumo.obterDados(endereco);
    var modeloLista = conversor.obterDados(json, Modelos.class);

    System.out.println("\nModelos dessa marca: ");
    modeloLista.modelos().stream()
        .sorted(Comparator.comparing(Dados::nome))
        .forEach(System.out::println);

    System.out.println("\nDigite um trecho do nome do carro a ser buscado");
    var nomeVeiculo = leitura.nextLine();

    List<Dados> modelosFiltrados = modeloLista.modelos().stream()
        .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
        .collect(Collectors.toList());
    System.out.println("\nModelos filtrados");
    modelosFiltrados.forEach(System.out::println);

    System.out.println("\nDigite o código do modelo: ");
    var codigoModelo = leitura.nextLine();

    endereco += "/" + codigoModelo + "/anos";
    json = consumo.obterDados(endereco);
    List<Dados> anos = conversor.obterLista(json, Dados.class);
    List<Veiculo> veiculos = new ArrayList<>();

    for (int i = 0; i < anos.size(); i++) {
      var enderecoAnos = endereco + "/" + anos.get(i).codigo();
      json = consumo.obterDados(enderecoAnos);
      Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
      veiculos.add(veiculo);
    }

    System.out.println("Todos os veiculos filtrados: ");

    veiculos.forEach(System.out::println);
  }
}
