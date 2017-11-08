**Notes of things needed to be addressed in this update**

- currently a lot of scala files is renamed with .rem as they are not yet converted from Lif 2.5 to 3.1
- comet.Chat uses net.liftweb.http.js.jquery.JqJsCmds.{AppendHtml} 
which are no longer available. ==> chat history is lost? 
- net.liftweb.example.snippet.Ajax uses net.liftmodules.widgets.autocomplete 
AutoComplete function that needs Query-Migrate v1.4.1 as it used old jquery 
stuff (jqurey.browser).
