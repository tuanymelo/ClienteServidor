import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;


public class Client {
	static Scanner entrada;
	static String endereco;
	static int porta;
	static Socket socket;
	static String servidor;
	static String extencao;
	static String requisicao;
	static Boolean naoFinal = false;
	static FileWriter file;
	static String novoEndereco;
	
	public static void lerEntrada(){
		System.out.println("Navegador =>");
		//entrada de dados
		System.out.println("Digite o endereço do arquivo e a porta, ex: http://urldesejada Porta ");
		entrada = new Scanner(System.in);
		String teclado = entrada.nextLine();
		
		//divide string em endereço e porta
		String[] requisicao = teclado.split(" ");
		endereco = requisicao[0];
		//caso nao digite a porta ela sera 80
		String port = "80";
		//caso contrario sera o valor digitado
		if(requisicao.length != 1){
			port = requisicao[1];	
		}
		//altera valor da porta para inteiro
		porta = Integer.parseInt(port);
	}
	
	public static void trataEndereco(){
		String[] caminho = endereco.split("/");
		String string1= "http:";
		String string2= "https:"; 
		
		//se comecar com hhtp ou https nao considera eles. para pegar o endereco do servidor
		if((caminho[0].equals(string1) || (caminho[0].equals(string2)))){
			servidor = caminho[2];
			String endereco2 = endereco.replace("http://", "").replace("https://", "");
			novoEndereco = (endereco2.substring(endereco2.indexOf("/") ));
		//	System.out.println("caminho  " + novoEndereco);
		}else{
			servidor = caminho[0];
			novoEndereco = (endereco.substring(endereco.indexOf("/") ));
			System.out.println("caminho  " + novoEndereco);
		}
	//	System.out.println("servidor : " + servidor);
		
	}
	
	public static void trataExtencao(){
		//olha se arquivo possui extencao ou nao é endereco final
		extencao = (endereco.substring(endereco.lastIndexOf("/") + 1));
				
		if (extencao.length() == 0){
			naoFinal = true;
			extencao = "index";
		}
	//	System.out.println("extencao " + extencao);
	}
	
	 public static void main(String[] args) throws UnknownHostException, IOException {
		 lerEntrada();
		 trataEndereco();
		 trataExtencao();
		 
	     //cria um socket com o servidor na porta escolhida 
	     socket = new Socket(servidor, porta);
	     
	  //   System.out.println("serv  "+ servidor);
	     
	     //verifica se esta conectado 
	     if (socket.isConnected()) {
	    	 //imprime o endereço de IP do servidor 
	         System.out.println("Conectado a " + socket.getInetAddress());
	     
		     if (naoFinal){
			     requisicao = ""
			                + "GET " + novoEndereco + "/ HTTP/1.1\r\n"
			                + "Host: " + servidor + "\r\n"
			                + "\r\n";
			     extencao += ".html";
			     System.out.println("req  "+requisicao);
		     }else{

		    	 requisicao = ""
			                + "GET " + novoEndereco + " HTTP/1.1\r\n"
			                + "Host: " + servidor + "\r\n"
			                + "\r\n";
		    	 System.out.println("req  "+requisicao);
		     }
		     
		   //OutputStream envia a requisição para o servidor
		    OutputStream envioServ = socket.getOutputStream();
		    //transforma requisicao em vetor de bytes que é o que o servidor entende
		    byte[] b = requisicao.getBytes();
		    //escreve o vetor de bytes no OutputStream
		    envioServ.write(b);
		    //marca a finalização da escrita 
		    envioServ.flush();
		    
		    /*enviou a requisicao, agr é preciso ver a resposta do servidor*/
		    
		    //ler do InputStream que vem do servidor 
		    entrada = new Scanner(socket.getInputStream());
		    
		    //cria um arquivo, o nome dele sera a extencao do arquivo solicitado
		    file = new FileWriter(new File(extencao));
		    
		    ArrayList<String> respostaServidor = new ArrayList<>();
		    
		  //printa e salva os dados do input em um array, cada linha uma posicao
	        while (entrada.hasNext()) {
	        	String line = entrada.nextLine();
				System.out.println(line);
				respostaServidor.add(line);
	        }
	        
	        int comeca = 0;
	       //conta as linhas de resposta de requisicao p n imprimir elas no arquivo
			for (int i = 0; i < respostaServidor.size(); i++) {
				if (respostaServidor.get(i).contains("Content-Type")) {
					comeca = i + 2;
					break;
	
				}
			}
			
		//salva conteudo do arquivo
			for (int i = comeca; i < respostaServidor.size(); i++) {
				file.write(respostaServidor.get(i));
				file.write("\n");
			}
	
	        file.flush();
	        file.close();
	        socket.close();
	    //    System.out.println("Conexao Encerrada!");
	     }else{
	    	 System.out.print("\n\n\nA conexão com o servidor " +servidor+ " não conseguiu ser estabelecida ou foi interrompida no meio, verifique se você está conectado na internet !");
	     }
	 }

}
