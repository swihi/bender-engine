# Bender Engine (a successor of JSRE)

Bender Engine is a simple rule engine for java. The focus is the ease of use to create applications 
that need to execute simple rules, which shall be editable by business stakeholders.

Bender Engine offers you to use a JSON file to describe input, applied rules and output.

A typical case to use Bender Engine would be, that you have some business rules, e.g. for configurators
or software assisten applications, that shall be configured by business stake holders
and you would like to avoid the complexity of Drools or other business rule management systems.

An easy usage could be creating a simple http-json (restful) webservice, which passes
the requests data directly to the rule engine and the response back to the client.
This way you can easily integrate your businessrules e.g. to your javascript frontend.


## Example usage:
The usage within your java application can be done as follows:

```
String json = "{ ... my json configuration ... }";	//your configuration file content (json)
Map<String, Object> input = new HashMap<String, Input>();	//your rule engine input data
input.put("firstName", "John");
RuleEngine re = RuleEngineFactory.getEngine(json);
re.setInput(input);
re.executeRules();
String output = re.getJsonDocument();
```

## Example configration:

test.json:
```
{
	"version" : "1.1",
	
	"inputValidation" : [
		{
			"name" : "firstName",
			"type" : "String",
			"mandatory" : false,
			"expression" : "John"
		},
	],
	"document" : {
		"greeting" : "Hello, who ever you are. Nice to meet you!"
	},
	"scriptBuilderType" : "BASIC",
	"rules" : [
		{
			"expression" : "input.firstName == 'John'",
			"description" : "This is some explaining text: Special greetings for John",
			"priority" : 1,
			"scriptActions" : [
				"document.greeting = 'Hey John! How are you?'"
			]
		},
		{
			"expression" : "typeof input.firstName === 'undefined'",
			"description" : "If noone passes a name in, we will just greet the world.",
			"priority" : 2,
			"scriptActions" : [
				"document.greeting = 'Hello World!'"
			]
		},
	]
}
```

* **inputValidation**
    * is an array of input attributes
    * you have to define all attributes, which can be sent to rules (especially you have to define its type)
    * these attributes can be accessed in your rule scripts as fields of variable `input`
    * `type` can be one of:
        * String
        * Integer
        * Double
        * Boolean
        * Fully qualified class name for other objects - this class **must** have public <init>(String) constructor
* **document** 
    * pre-initialized output document (that means you can always add another attributes to `document` in rule script)
* **scriptBuilderType**
    * you can specify which script engine you would like to use (see. chapter Script engine types)
    * if this configuration option is missing, by default is used Basic script engine
* **rules**
    * *expression* - is a condition which is evaluated before this rule. If it is true, all `scriptActions` are executed otherwise not. If you want to omit this test rule, simply place `true` here 
    * *priority* - rules (including conditions) are executed in priority order from the lowest to the biggest 
   
## Example output
The output of the above example would look like:
```
{
	"greeting" : "Hey John! How are you?"
}
```
For passing in any other name than John, e.g. Jake, the answer would look like:
```
{
	"greeting" : "Hello, who ever you are. Nice to meet you!"
}
```
If no name is passed in as parameter, the answer will be:
```
{
	"greeting" : "Hello World!"
}
```

## Script engine types
Bender engine offers 2 types of script engines: Basic and Multioutput.

#### Basic script engine
This is a default script engine. It accepts one input object (called `input`) and one output object (called `document`).
Attributes of input obect are defined in `inputValidation` section of configuration and output document is 
*pre-initialized* in `document` section of configuration (that means you can always add another attributes to 
`document` in rule script). Directly output `document` is returned in getJsonDocument() method.

#### Multioutput script engine
In some cases you may need to get multiple output document from your rule. This rule engine also has one input
object in variable `input` and one pre-initialized `document` as the Basic one, but here you can use method addToOutput()
in your rule script. Each time you use this method a copy of `document` object is added to the output list.
If you never call addToOutput() method in your rule script, it will be call once for you at the end.

When you call re.getJsonDocument() it will be returned a list of `documents` converted to JSON format. 
  

## Usage of the rule engine cache

Most likely you will use the same configuration over and over again to answer requests. 
In this case you probably want to cache the configuration to speed up your process.
If you are not going to cache the configuration the configuration has to be compiled
for every new rule engine you create. If you use the provided rule engine cache,
this is not needed anymore. This normaly speeds up the evaluation time with configurations
of size > 4kb down to 10% of the execution time.

There are a few things you need to do to use the rule engine cache classes:

1. You need to register a rule engine provider, which describes how to get the json content
and how to configure your engine.
Example:

  ```
  RuleEngineCache.registerRuleEngineProvider(new BasicRuleEngineProvider() {

	@Override
	public String getConfigurationContent(String filename) throws IOException {
		return ResourceFileHelper.getFileContent(filename);
	}

	@Override
	protected void configureEngine(RuleEngine ruleEngine) {
		//configure your rule engine here, e.g. register java actions. 
	}
  });
  ```
  
2. You need to use the rule engine cache class to get (and build) the engines:
  
  ```
  String configFile = "the file name of the configuration you would like to use."; //this is used as the key for the cache
  RuleEngine re = RuleEngineCache.get().getLockedRuleEngine(configFile);
  ```
  
3. You go on like in the none cache example from above:
  
  ```
  Map<String, Object> input = new HashMap<String, Input>();	//your rule engine input data
  input.put("firstName", "John");
  re.setInput(input);
  re.executeRules();
  String output = re.getJsonDocument();
  ```

4. Last but not least you need to unlock the rule engine. This will tell the cache,
that this specific instance is not used anymore and can be provided to other threads
requesting a configuration with that filename.

  ```
  RuleEngineCache.get().unlock(re);
  ```

## Pre-, Post-Execution
Sometimes you need to execute code before applying the rules or most likely after the rules have been applied.
This can for instance be used to calculate sums and add them to the document.
In this cases you can use the "postExecution" or "preExecution" fields.
Example:
```
	"postExecution" : [
		"document.totalPrice += document.ghz[input.ghzOption].price"
	]
```

