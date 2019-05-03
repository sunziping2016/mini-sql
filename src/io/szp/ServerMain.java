package io.szp;

import io.szp.exception.CmdlineParseException;
import io.szp.server.ServerConfig;
import io.szp.server.Server;

/**
 * 这是服务端的主类，负责解析命令行，而后启动服务。
 */
public class ServerMain {
    private static final String helpMessage =
            "Usage: server [OPTION]... [port [host]]\n" +
            "Options:\n" +
            "  -p, --port=PORT           port to listen (default: " + ServerConfig.DEFAULT_PORT + ")\n" +
            "  -h, --host=HOST           host to listen (default: " + ServerConfig.DEFAULT_HOST + ")\n" +
            "  --verbose                 verbose information message\n" +
            "  --help                    print this help message\n";
    /**
     * 服务端的主函数，解析命令行，启动服务。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            String host = ServerConfig.DEFAULT_HOST;
            int port = ServerConfig.DEFAULT_PORT;
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
                } else if (args[i].equals("--verbose")) {
                    ServerConfig.verbose = true;
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
            // 启动服务
            Server server = new Server(port, host);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
