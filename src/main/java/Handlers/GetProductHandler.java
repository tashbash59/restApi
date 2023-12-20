package Handlers;

import Entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetProductHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String requestMethod = exchange.getRequestMethod();
        String str = "";
        int rCode = 200;
        List<List<String>> response = new ArrayList<>();
        if (requestMethod.equalsIgnoreCase("GET")) {
            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            try {
                //Product product = mapper.readValue(requestBody.toString(), Product.class);
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
                Root<Product> root = criteria.from(Product.class);
                criteria.select(root);
                Query<Product> query = session.createQuery(criteria);
                for(int i = 0; i < query.getResultList().size(); i++) {
                    List<String> list = new ArrayList<>();
                    list.add(query.getResultList().get(i).getProduct_id().toString());
                    list.add(query.getResultList().get(i).getTitle());
                    list.add(query.getResultList().get(i).getDescription());
                    list.add(query.getResultList().get(i).getImage());
                    response.add(list);
                }

                transaction.commit();
            } catch (Exception e){
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
                rCode = 400;
                str = e.getMessage();
            } finally {
                if (session != null) {
                    session.close();
                    sessionFactory.close();
                }

            }


        } else {
            session.close();
            sessionFactory.close();
            rCode = 400;
            str = "method of requrest is wrong";
        }

        //response.put("message", str);
        System.out.println(response);
        System.out.println(rCode);

        byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(rCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
