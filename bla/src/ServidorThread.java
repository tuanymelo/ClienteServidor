import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ServidorThread extends Thread {
	
	private String url;
	private String protocolo;
	private long tamanhoDoArquivo;
	private Socket cliente;
	private String endereco = "";
	
	ServidorThread(Socket cliente) {
		this.cliente = cliente;
	}
	
	@Override
    public void run() {
    	try {
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
		  //   System.out.println("arquivo " + nomeArquivo);
		  BufferedWriter responseWriter = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(cliente.getOutputStream()), "UTF-8"));
		         
	    //abre o arquivo 
		File arquivo = new File(nomeArquivo);
		       
		 if(arquivo.isDirectory()){
			 OutputStream output = cliente.getOutputStream();
	
		     String file[] = arquivo.list(); 
		      	
		     System.out.println("aa");
		     responseWriter = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(cliente.getOutputStream()), "UTF-8"));
             
	           String html = "<!DOCTYPE html><html><head><title>Erro 404</title><meta charset=utf-8></head><body>";
	           
	           for (int i = 0;i< file.length; i++) {
	        	   html += "<div>"+file[i]+"</div>";
	        	   System.out.println(file[i]);			
	           }
	           
	           html += "</body></html>";
	           
	           System.out.println(html);
	           
	           String resposta1 = "HTTP/1.1 404 Not Found\r\n" + "Content-Type: text/html\r\n"
	           					+ "Content-Length: ";
	           System.out.println(resposta1);
	           		
	           responseWriter.write( resposta1 + html.length() + "\r\n\r\n" + html);
	           System.out.println(html.length());
	        		
	           responseWriter.flush();
	           responseWriter.close();
		     
			 
	//			
	    	//mandar resposta pro cliente
	    	//	OutputStream output = cliente.getOutputStream();
	
	    		String resposta = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n"
	    				+ "Content-Length: ";
	    			
	    		System.out.println(resposta);
	    		//transforma em bytes
	    		output.write(resposta.getBytes(Charset.forName("UTF-8")));
	    		
	    		//manda tamanho do arquivo
	    	//	output.write(("Content-Length: " + String.valueOf(file.length()) + "\r\n\r\n").getBytes());	
	
	    	
	    		//FileInputStream texto = new FileInputStream(arquivo);
	
	    		tamanhoDoArquivo = file.length;
	    		while (tamanhoDoArquivo > 0) {
	    			tamanhoDoArquivo --;
	    			byte[]	b = new byte[1];
	    			String teste = file[(int) tamanhoDoArquivo];
	    			System.out.println(file[(int) tamanhoDoArquivo]);	
	    			output.write(teste.getBytes(Charset.forName("UTF-8")));
	    		//	teste.read(b, 0, 1);
	    		//	output.write(b);
	    		}
	    		//marca a finalização da escrita 
	 		   output.flush();
	
		} else if (!arquivo.exists()) {
	           responseWriter = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(cliente.getOutputStream()), "UTF-8"));
	               
	           String htmlErro = "<!DOCTYPE html><html><head><title>Erro 404</title><meta charset=utf-8></head><body><h1>404 Not Found</h1>A página que você procura não foi encontrada</body></html>";
	           String resposta = "HTTP/1.1 404 Not Found\r\n" + "Content-Type: text/html\r\n"
	           					+ "Content-Length: ";
	           System.out.println(resposta);
	           		
	           responseWriter.write( resposta + htmlErro.length() + "\r\n\r\n" + htmlErro);
	           System.out.println(htmlErro.length());
	        		
	           responseWriter.flush();
	           responseWriter.close();
	           
	    } else if (arquivo.exists()) {
	    			
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
	    		while (tamanhoDoArquivo > 0) {
	    			tamanhoDoArquivo --;
	    			byte[]	b = new byte[1];
	    			texto.read(b, 0, 1);
	    			output.write(b);
	    		}
	        }
		 cliente.getInputStream().close();
    	} catch (IOException io) {
    		
    	}
    }
}