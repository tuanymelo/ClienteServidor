import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
	static BufferedWriter responseWriter;
	
	
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
	}
	
	
	
	public static void main(String[] args) throws IOException {
	    leEntrada();
	   /* cria um socket "server" associado a porta escolhida*/ 
	   server = new ServerSocket(porta);
	   System.out.println("Servidor ativo na porta: " +porta);
	   while (true) {
		   Socket cliente = server.accept();
		   if (cliente.isConnected()) {
			   ServidorThread servidorThread = new ServidorThread(cliente);
			   servidorThread.run();
		    }
	   }
	}
}


