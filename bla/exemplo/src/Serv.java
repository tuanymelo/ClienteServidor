import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;

public class Serv implements Runnable {

	static ObjectOutputStream saida;
	static OutputStream os;
	static int porta;
	static String navegador;
	static String url;
	static String door;
	static String caminho;
	static String path;
	static PrintStream mensagem;
	static Socket servidor;
	static ServerSocket sservidor;
	static BufferedWriter responseWriter;
	static Scanner c;

	private static final String OUTPUT_NOT_FOUND = "<html><head><title>Not Found</title></head><body><p>Resource Not Found!!</p></body></html>";
	private static final String OUTPUT_HEADERS_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n" + "Content-Type: text/html\r\n"
			+ "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

	Serv(Socket servidor) {
		this.servidor = servidor;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("Bem vindo ao servidor!\nDigite o caminho e a porta a ser usada!");
		System.out.println("servidor>");
		c = new Scanner(System.in);
		String caminho = c.nextLine();
		String[] parts = caminho.split(" ");
		if (parts.length == 2) {
			path = parts[0];
			porta = Integer.parseInt(parts[1]);
			
		} else if (parts.length == 1) {
			path = parts[0];
			porta = 8080;
		} else {
			System.out.println("Você digitou errado!");
		}
		sservidor = new ServerSocket(porta);
		System.out.println("Servidor está rodando na porta - " + porta + "\n");

		while (true) {
			Socket servidor = sservidor.accept();
			new Thread(new Serv(servidor)).start();
		}

	}

	public void run() {
		System.out.println("Nova conexão com o servidor " + servidor.getInetAddress().getHostAddress() + "\n");

		try {
			responseWriter = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(servidor.getOutputStream()), "UTF-8"));

			Scanner s = new Scanner(servidor.getInputStream());
			
			while (s.hasNextLine()) {
				String requisicao = s.nextLine();
				System.out.println(requisicao);
				String[] parts = requisicao.split(" ");
				if (parts.length == 3) {
					navegador = parts[0];
					url = parts[1];
					door = parts[2];
				} else if (parts.length == 2) {
					navegador = parts[0];
					url = parts[1];
					door = "8080";
				} else {
					System.out.println("Você digitou errado!");
				}
				break;
			}
			GET();
			responseWriter.flush();
			responseWriter.close();
			servidor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public void GET() throws IOException, ClassNotFoundException {

		String nomeArquivo = path + url;
		File file = new File(nomeArquivo);

		if(file.isDirectory()){
	          
			OutputStream stream = servidor.getOutputStream();

	            String arquivos[] = file.list();  
	            String mensagem = "Os arquivos do diretório local são: ";
	            stream.write(mensagem.getBytes(Charset.forName("UTF-8")));
	            
	            String espaco = "\n";
	            for(int i = 0; i < arquivos.length; i++){ 
	              
	              System.out.println(arquivos[i]);
	              stream.write(espaco.getBytes(Charset.forName("UTF-8")));
	              stream.write(arquivos[i].getBytes(Charset.forName("UTF-8")));
	              stream.write(espaco.getBytes(Charset.forName("UTF-8")));

	            }  
	            stream.flush();
	    }
		
		else if (file.exists()) {
			
			String mime = Files.probeContentType(file.toPath());
			System.out.println("Content Type: " + mime);
			OutputStream stream = servidor.getOutputStream();

			String send = "HTTP/1.1 200 OK\r\nContent-Type: " + mime + "\r\n";
			stream.write(send.getBytes(Charset.forName("UTF-8")));
			//stream.flush();

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			stream.write(("Content-Length: " + String.valueOf(file.length()) + "\r\n\r\n").getBytes());
			//stream.flush();


			long tam = file.length();
			int valor = 0;
			byte[] contents;
			while (tam > 0) {

				if (tam >= 1) {
					tam = tam - 1;
					valor = 1;
				} else if (tam < 1) {
					valor = (int) tam;
					tam = 0;
				}

				contents = new byte[valor];
				bis.read(contents, 0, valor);
				stream.write(contents);

			}
			stream.flush();
			fis.close();
			bis.close();

		}
			
		
		else {
			responseWriter.write(
					OUTPUT_HEADERS_NOT_FOUND + OUTPUT_NOT_FOUND.length() + OUTPUT_END_OF_HEADERS + OUTPUT_NOT_FOUND);
		}

	}

}