package oracle.alpha.cloud;

/**
 * The SimpleProducer class consists only of a main method,
 * which sends several messages to a queue or topic.
 *
 * Run this program in conjunction with SimpleSynchConsumer or
 * SimpleAsynchConsumer. Specify a queue or topic name on the
 * command line when you run the program.  By default, the
 * program sends one message.  Specify a number after the
 * destination name to send that number of messages.
 */

import java.util.Hashtable;

import javax.jms.*;
import javax.naming.*;


public class JMSTestClient {
    /**
     * Main method.
     *
     * @param args     the destination used by the example
     *                 and, optionally, the number of
     *                 messages to send
     */
    public static void main(String[] args) {
        final String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";
        final String JMS_FACTORY = "weblogic.jms.XAConnectionFactory";
        final String QUEUE = "jms/AlphaQueue";

        System.out.println("Destination name is " + QUEUE);

        Context context = null;
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
            env.put(Context.PROVIDER_URL, "t3://localhost:7001");
            env.put(Context.SECURITY_PRINCIPAL, "weblogic");
            env.put(Context.SECURITY_CREDENTIALS, "Welcome1");

            context = new InitialContext(env);
        } catch (NamingException e) {
            System.out.println("Could not create JNDI API context: " +
                e.toString());
            System.exit(1);
        }

        Connection connection = null;
        try {
            ConnectionFactory connectionFactory;
            connectionFactory = (ConnectionFactory) context.lookup(JMS_FACTORY);
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) context.lookup(QUEUE);
            MessageProducer producer = session.createProducer(destination);

            ObjectMessage message = session.createObjectMessage();
            //EMIaaSMessage emMsg = new EMIaaSMessage ("createAlphaDevImage", "AlphaDev_004");
            EMIaaSMessage emMsg = new EMIaaSMessage ("getIaaSInfo");
            
            message.setObject(emMsg);
            producer.send(message);
            session.close();
            connection.close();
        } catch (NamingException e) {
            System.out.println("Could not create Connection Factory: " +
                e.toString());
            System.exit(1);
        } catch (JMSException e) {
            System.out.println("Could not create Connection: " +
                e.toString());
            System.exit(1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
