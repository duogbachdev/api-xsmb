package com.duogbachdev.apixsmb.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @Value("${url}")
    private String url;

    @GetMapping("/results")
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            Document doc = Jsoup.connect(url).get();

            // Trích xuất các số kết quả
            Elements rows = doc.select("div#load_kq_mb_0 table.kqmb tbody tr");
            Map<String, List<String>> results = new LinkedHashMap<>();

            for (Element row : rows) {
                Element header = row.selectFirst("td.txt-giai");
                Element numbers = row.selectFirst("td.v-giai");

                if (header != null && numbers != null) {
                    String title = header.text();
                    Elements spans = numbers.select("span");
                    List<String> numberList = new ArrayList<>();
                    for (Element span : spans) {
                        numberList.add(span.text());
                    }
                    results.put(title, numberList);
                }
            }

            // Trích xuất thông tin đầu và đuôi
            Map<String, List<String>> firstLastResults = new HashMap<>();
            Elements firstLastRows = doc.select("div.col-firstlast table.firstlast-mb tbody tr");
            for (Element row : firstLastRows) {
                String head = row.selectFirst("td.clred") != null ? row.selectFirst("td.clred").text() : "";
                String tail = row.selectFirst("td.v-loto-dau-0, td.v-loto-duoi-0") != null ? row.selectFirst("td.v-loto-dau-0, td.v-loto-duoi-0").text() : "";
                if (!head.isEmpty() && !tail.isEmpty()) {
                    firstLastResults.put(head, Arrays.asList(tail.split(",")));
                }
            }

            response.put("results", results);
            response.put("firstLast", firstLastResults);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("msg", "Error fetching data");
        }

        return response;
    }
}

