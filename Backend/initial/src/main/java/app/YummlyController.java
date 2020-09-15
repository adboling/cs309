package app;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * used to search for recipes in Yummly API See
 * https://developer.yummly.com/documentation for more information
 *
 */
@RestController
public class YummlyController {

	private String appID = "d9c8cd59";
	private String appKey = "20bead569fb1ca4c088d69e107068b81";
	private String creds = "_app_id=" + appID + "&_app_key=" + appKey;

	/**
	 * 
	 * @return DEMO TEXT
	 */
	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!!!!!!!!!";
	}

	/**
	 * used to search for a Yummly recipe
	 * 
	 * @param search a string with all the search Param
	 * @return a JSON of search results
	 * @throws IOException
	 */
	@GetMapping("/search/{search}")
	public ResponseEntity<String> search(@PathVariable String search) throws IOException {
		String url = "http://api.yummly.com/v1/api/recipes?" + creds + "&" + search;
		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	/**
	 * used to open the full details of a recipe
	 * 
	 * @param recipeID the id the recipe
	 * @return a JSON for the recipe
	 * @throws IOException
	 */
	@GetMapping("/recipe/{recipeID}")
	public ResponseEntity<String> searchByID(@PathVariable String recipeID) throws IOException {
		String url = "http://api.yummly.com/v1/api/recipe/" + recipeID + "?" + creds;
		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

//	@GetMapping("/greeting")
//	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
//			Model model) {
//		model.addAttribute("name", name);
//		return name + " is a great name!";
//	}

//	@GetMapping("/api")
//	public String api(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model)
//			throws IOException {
//		model.addAttribute("name", name);
//		String apiKey = "3bf8db01526b4eb8acb2211bcff900cc";
////        String url = "https://newsapi.org/v2/everything?q="+name+"&from=2019-01-05&sortBy=publishedAt&apiKey="+apiKey;
//		String url = "https://google.com";
//		// String html =
//		// Jsoup.connect("https://newsapi.org/v2/everything?q=bitcoin&from=2019-01-05&sortBy=publishedAt&apiKey=3bf8db01526b4eb8acb2211bcff900cc").get().html();
//
//		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
//		return json;
////        https://newsapi.org/v2/everything?q=bitcoin&from=2019-01-05&sortBy=publishedAt&apiKey=3bf8db01526b4eb8acb2211bcff900cc
//	}

//    @PostMapping("/api/{search}")
//    public String show(@PathVariable String search) throws IOException{
//    	 String apiKey = "3bf8db01526b4eb8acb2211bcff900cc";
//         String url = "https://newsapi.org/v2/everything?q="+search+"&from=2019-01-05&sortBy=publishedAt&apiKey="+apiKey; 
//         String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
//         return json;
//         
//    }
//    
//    
//    

}
