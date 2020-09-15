package app;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import org.json.*;
import org.jsoup.Jsoup;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    @RequestMapping("/page1")
    public String index1() {
        return "Greetings from page1!";
    }
    
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return name+ " is a great name!";
    }
    
    
    @GetMapping("/api")
    public String api(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) throws IOException {
        model.addAttribute("name", name);
        String apiKey = "3bf8db01526b4eb8acb2211bcff900cc";
//        String url = "https://newsapi.org/v2/everything?q="+name+"&from=2019-01-05&sortBy=publishedAt&apiKey="+apiKey;
        String url = "https://google.com";
      // String html = Jsoup.connect("https://newsapi.org/v2/everything?q=bitcoin&from=2019-01-05&sortBy=publishedAt&apiKey=3bf8db01526b4eb8acb2211bcff900cc").get().html();
        
        String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
        return json;
//        https://newsapi.org/v2/everything?q=bitcoin&from=2019-01-05&sortBy=publishedAt&apiKey=3bf8db01526b4eb8acb2211bcff900cc
    }
    
    @PostMapping("/api/{search}")
    public String show(@PathVariable String search) throws IOException{
    	 String apiKey = "3bf8db01526b4eb8acb2211bcff900cc";
         String url = "https://newsapi.org/v2/everything?q="+search+"&from=2019-01-05&sortBy=publishedAt&apiKey="+apiKey; 
         String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
         return json;
         
    }
    
    
    
    @GetMapping("/search")
    public String search(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) throws IOException {
        model.addAttribute("name", name);
        String searchFields = "http://www.recipepuppy.com/api/?i=&q="+name+"&p=1";
        String json = Jsoup.connect(searchFields).ignoreContentType(true).execute().body();
        
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("results");
        String result = "" ;
        
        for (int i = 0; i < arr.length() && i < 4; i++)
        {
            String url = arr.getJSONObject(i).getString("href");
            String iFrame = "<iframe width=\"420\" height=\"315\" src=\""+url+"\" frameborder=\"0\" allowfullscreen></iframe>\n";
            result += iFrame;
        }
        
      ////  String url = "https://www.yummly.com/recipe/Tomato-Basil-Soup-2632795";
      //  String iFrame = "<iframe width=\"420\" height=\"315\" src=\""+url+"\" frameborder=\"0\" allowfullscreen></iframe>\n";
        
        return result;
//        https://newsapi.org/v2/everything?q=bitcoin&from=2019-01-05&sortBy=publishedAt&apiKey=3bf8db01526b4eb8acb2211bcff900cc
    }
    
    
}
