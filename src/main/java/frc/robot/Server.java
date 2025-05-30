package frc.robot;

import edu.wpi.first.wpilibj.Filesystem;
import frc.robot.CMDZ.CommandScheduler;
import frc.robot.CMDZ.PrintCommand;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.http.staticfiles.Location;
import io.javalin.router.Endpoint;
import org.jetbrains.annotations.NotNull;

public class Server {
    public static boolean shouldStop=false;
    public Server() {
        var j=Javalin.create((JavalinConfig c)->c.staticFiles.add((Filesystem.getDeployDirectory().toString()),Location.EXTERNAL));
        j.addEndpoint(new Endpoint(HandlerType.GET, "/", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                ctx.html("<!DOCTYPE html><html><head></head><body><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js\"></script></body></html>");
            }
        }));
        j.addEndpoint(new Endpoint(HandlerType.GET, "/commands_running", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                String progress= "[";
                progress+=(new PrintCommand("sdf").deadlineFor(new PrintCommand("asdf"))).repr();
                for(var cmd:CommandScheduler.getInstance().m_scheduledCommands){
                    progress+=(new PrintCommand("sdf")).repr();
                    progress+=",";
                }
                progress+="]";
                ctx.html(progress.toString());
            }
        }));

        j.addEndpoint(new Endpoint(HandlerType.POST, "/p", new Handler() {
            @Override
            public synchronized void handle(@NotNull Context ctx) throws Exception {
                System.out.println(ctx.body());
                s.b();
            }
        }));
        j.addEndpoint(new Endpoint(HandlerType.POST, "/ads", new Handler() {
            @Override
            public synchronized void handle(@NotNull Context ctx) throws Exception {
                shouldStop=!shouldStop;
            }
        }));
        j.start(8080);
    }
    static public class Syncher{
        public Syncher() {
        }
        synchronized public void w(){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        synchronized void b(){
            notifyAll();
        }
    }
    static public Syncher s= new Syncher();

}
