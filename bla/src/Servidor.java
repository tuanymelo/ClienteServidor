import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;


public class Servidor {
	static Scanner entrada;
	static int porta;
	static String endereco = "";
	static Socket cliente;
	static ServerSocket server;
	static String url;
	static String protocolo;
	static long tamanhoDoArquivo;
	
	public static void leEntrada(){
		System.out.println("SERVIDOR =>");

        //entrada de dados
        System.out.println("Digite pasta de arquivos e uma porta.  \nExemplo: servidor public_html 8080,\n ou somente a porta para iniciar o servidor");
        entrada = new Scanner(System.in);
       	String teclado = entrada.nextLine();
        		
       	//divide string em endereço e porta
       	String[] pasta = teclado.split(" ");
       	String porta1 =  "";
       	
       	if (pasta.length < 2){
       		porta1 = pasta[0];
       	}else{
       		endereco = pasta[0];
       		porta1 = pasta[1];
       	}
       	// coloca porta como inteiro
       	porta = Integer.parseInt(porta1);	
       	System.out.println("Servidor ativo na porta: " +porta + " "+ endereco);
	}
	
	public static void main(String[] args) throws IOException {
	    leEntrada();
	    
	    /* cria um socket "server" associado a porta escolhida*/ 
	   server = new ServerSocket(porta);
	   System.out.println("Servidor ativo na porta: " +porta); 
	   
	   Socket cliente = server.accept();
	   if (cliente.isConnected()) {
		   System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
		   
			 String metodo = " ";
			//Buffer para ler requisicao input do cliente
			BufferedReader buffer = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			System.out.println("Requisição: ");
		    //Lê a primeira linha
		     String linha = buffer.readLine();
		     //quebra a string pelo espaço em branco
		      String[] requisicaoHTTP = linha.split(" ");
		        
		      if(requisicaoHTTP.length == 3){
		    	  //pega o metodo
			      metodo = requisicaoHTTP[0];
			      //paga o caminho do arquivo
			      url = requisicaoHTTP[1];
			      //pega o protocolo
			      protocolo = requisicaoHTTP[2];
		        }else if(requisicaoHTTP.length == 2){
		        	//pega o metodo
			        metodo = requisicaoHTTP[0];
			        //pega o protocolo
			        protocolo = requisicaoHTTP[1];
		       }
		      //Enquanto a linha não for vazia
		       while (!linha.isEmpty()) {
		        	//imprime a linha
		        	System.out.println(linha);
		        	//lê a proxima linha
					linha = buffer.readLine();
			   }
		       String nomeArquivo;
		       if(endereco != ""){
		    	    nomeArquivo = endereco + url;
		       }else{
		    	   url = (url.substring(url.lastIndexOf("/") + 1));
		    	   nomeArquivo =  url+"/";
		       }
		      
		       //System.out.println("url " + url);
		       System.out.println("arquivo " + nomeArquivo);
		       
		       //abre o arquivo pelo caminho
		       File arquivo = new File(nomeArquivo);
		       
	           if (!arquivo.exists()) {
	            	String htmlResposta = "<!DOCTYPE html><html><head><title>Erro 404</title><meta charset= utf-8 ></head><body><h1>A página que você procura não foi encontrada</h1></body></html";
	    			String resposta = "HTTP/1.1  404 Not Found\r\n"+
	    					"Content-Type: text/html\r\n"+
	    				"Content-Length: " + htmlResposta.length();
	    			System.out.println(resposta);
	    			
	    			 //mandar resposta pro cliente
	       			OutputStream output = cliente.getOutputStream();
	       		   //transforma em bytes
	    			output.write(resposta.getBytes(Charset.forName("UTF-8")));
	    			output.flush();
	            }
	            
	            if (arquivo.exists()) {
	    			
	            //	Pesquisa o arquivo fornecido para adivinhar seu tipo de conteúdo.
	    			String mime = Files.probeContentType(arquivo.toPath());
	    			System.out.println("Content Type: " + mime);
	    			
	    			//mandar resposta pro cliente
	    			OutputStream output = cliente.getOutputStream();

	    			String resposta = "HTTP/1.1 200 OK\r\nContent-Type: " + mime + "\r\n";
	    			
	    			System.out.println(resposta);
	    			//transforma em bytes
	    			output.write(resposta.getBytes(Charset.forName("UTF-8")));
	    			

	    	
	    			//manda tamanho do arquivo
	    			output.write(("Content-Length: " + String.valueOf(arquivo.length()) + "\r\n\r\n").getBytes());	

	    	
	    			FileInputStream texto = new FileInputStream(arquivo);
	    	    	
	    			tamanhoDoArquivo = arquivo.length();
	    			int valor = 0;
	    	
	    			while (tamanhoDoArquivo > 0) {
	    				if (tamanhoDoArquivo >= 1) {
	    					tamanhoDoArquivo --;
	    					
	    				} 

	    			byte[]	b = new byte[1];
	    				texto.read(b, 0, 1);
	    				output.write(b);
	    			}
	        }
	           
	    }
	}
}


