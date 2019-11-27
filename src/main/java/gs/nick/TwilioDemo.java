package gs.nick;

import static spark.Spark.*;

public class TwilioDemo {

    public static void main(String[] args) {
        Config cfg = new Config();
        port(5002);
        System.out.println("running on 5002");

        get("/", (req, res) -> {
            System.out.println("GET /");
            return "System is running.\nConfig:\n" + cfg;
        });

        get("/voice", (req, res) -> {
            System.out.println("GET /voice");

            res.type("application/xml");
            if (cfg.forwardNumber == null) {
                return "<Response> <Say>I am not ready. Text me a phone number.</Say> </Response>";
            }
            return "<Response> <Dial> " + cfg.forwardNumber + " </Dial> </Response>";
        });

        get("/sms", (req, res) -> {
            System.out.println("GET /sms");

            res.type("application/xml");
            String body = req.queryParams("Body");
            if (body == null) {
                return smsTwiml("bad input");
            }
            if (body == "reset") {
                cfg.forwardNumber = null;
                return smsTwiml("reset complete");
            }
            if (body.length() == 12) {
                cfg.forwardNumber = body;
                return smsTwiml("I will forward calls to " + body);
            }
            if (body.length() == 10) {
                cfg.forwardNumber = "+1" + body;
                return smsTwiml("I will forward calls to " + cfg.forwardNumber);
            }
            return smsTwiml("I don't understand. Current config: \n" + cfg);
        });
    }

    public static String smsTwiml(String msg) {
        return "<Response><Message>" + msg + "</Message></Response>";
    }
}
