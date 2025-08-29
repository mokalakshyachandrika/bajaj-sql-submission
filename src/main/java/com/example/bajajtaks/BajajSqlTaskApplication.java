package com.example.bajajtaks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajSqlTaskApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajSqlTaskApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> body = new HashMap<>();
        body.put("name", "Moka Lakshya Chandrika"); 
        body.put("regNo", "22BCE8360"); 
        body.put("email", "lakshyamoka@gmail.com"); 

        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, body, Map.class);

        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");

        System.out.println("Webhook: " + webhook);
        System.out.println("Token: " + token);

    
        String finalQuery = """
            SELECT e.emp_id,
                   e.first_name,
                   e.last_name,
                   d.department_name,
                   COUNT(e2.emp_id) AS younger_employees_count
            FROM employee e
            JOIN department d ON e.department = d.department_id
            LEFT JOIN employee e2
                   ON e.department = e2.department
                  AND e2.dob > e.dob
            GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name
            ORDER BY e.emp_id DESC;
            """;

      
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> answer = new HashMap<>();
        answer.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(answer, headers);
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhook, request, String.class);

        System.out.println("Submission response: " + submitResponse.getBody());
    }
}
