package io.szp.minisql;

import io.szp.minisql.exception.CmdlineParseException;
import io.szp.minisql.schema.Table;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientMain {
    private static final int DEFAULT_PORT = 24620;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String helpMessage =
            "Usage: client-cli [OPTION]... [port [host]]\n" +
            "Options:\n" +
            "  -p, --port=PORT           port to connect (default: " + DEFAULT_PORT + ")\n" +
            "  -h, --host=HOST           host to connect (default: " + DEFAULT_HOST + ")\n" +
            "  --help                    print this help message\n";

    public static void main(String[] args) {
        try {
            int port = DEFAULT_PORT;
            String host = DEFAULT_HOST;
            // 解析命令行
            int positionArgumentNum = 0;
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-p") || args[i].equals("--port")) {
                    if (++i == args.length)
                        throw new CmdlineParseException("Missing argument for port");
                    port = Integer.parseInt(args[i]);
                } else if (args[i].startsWith("--port=")) {
                    port = Integer.parseInt(args[i].substring(7));
                } else if (args[i].equals("-h") || args[i].equals("--host")) {
                    if (++i == args.length)
                        throw new CmdlineParseException("Missing argument for host");
                    host = args[i];
                } else if (args[i].startsWith("--host=")) {
                    host = args[i].substring(7);
                } else if (args[i].equals("--help")) {
                    System.out.print(helpMessage);
                    return;
                } else if (args[i].startsWith("-")) {
                    throw new CmdlineParseException("Unknown command line option");
                } else {
                    switch (positionArgumentNum) {
                        case 0:
                            port = Integer.parseInt(args[i]);
                            break;
                        case 1:
                            host = args[i];
                            break;
                        default:
                            throw new CmdlineParseException("Too many position arguments");
                    }
                    ++positionArgumentNum;
                }
            }

            try (Socket socket = new Socket(InetAddress.getByName(host), port)) {
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
                ) {
                    Scanner scanner = new Scanner(System.in);
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        System.out.print("> ");
                        String command = scanner.nextLine();
                        if (command.startsWith("import")) {
                            int i = "import".length();
                            while (Character.isWhitespace(command.charAt(i)))
                                ++i;
                            String path = command.substring(i);
                            StringBuilder builder = new StringBuilder();
                            try (BufferedReader file = new BufferedReader(
                                    new InputStreamReader(new FileInputStream(path)))) {
                                String line = file.readLine();
                                while (line != null) {
                                    builder.append(line).append('\n');
                                    line = file.readLine();
                                }
                            }
                            command = builder.toString();
                        }
                        long start = System.currentTimeMillis();
                        out.writeObject(command);
                        Object object = in.readObject();
                        long end = System.currentTimeMillis();
                        System.out.println("Time: " + ((end - start) / 1000.0) + "s");
                        if (object instanceof String)
                            System.err.println((String) object);
                        else if (object instanceof Table)
                            ((Table) object).print(System.out);
                        else if (object != null)
                            throw new Exception("Unknown object from input stream");
                    }
                } catch (NoSuchElementException e) {
                    // do nothing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
