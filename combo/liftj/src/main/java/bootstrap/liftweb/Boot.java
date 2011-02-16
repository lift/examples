package bootstrap.liftweb;

import net.liftweb.common.Func;
import net.liftweb.common.Func0;
import net.liftweb.common.Func1;
import net.liftweb.util.*;
import net.liftweb.http.*;
import net.liftweb.http.js.JsCmd;
import net.liftweb.http.provider.HTTPRequest;
import net.liftweb.sitemap.*;


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
public class Boot {
    public SiteMap makeSiteMap() {
	String[] a = {"index"};

	return 
	    SiteMapJ.build(MenuJ.j().i("Home").path("index"),
			   MenuJ.j().i("Dude").path("two"));
    }


    public void boot() {

	// where to search snippet
	LiftRulesJ.j().addToPackages("code");
	    /*	    
    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

 
 	    */

	LiftRulesJ.j().setSiteMapFunc(Func.lift(new Func0<SiteMap>() {
		public SiteMap apply() {
		    return makeSiteMap();
		}}));

	
	//Show the spinny image when an Ajax call starts
	LiftRulesJ.j().
	    setAjaxStart(new Func0<JsCmd>() {
		    public JsCmd apply() {
			return LiftRulesJ.j().
			    jsArtifacts().show("ajax-loader").cmd();
		    }});

	// Make the spinny image go away when it ends
	LiftRulesJ.j().
	    setAjaxEnd(new Func0<JsCmd>() {
		    public JsCmd apply() {
			return LiftRulesJ.j().
			    jsArtifacts().hide("ajax-loader").cmd();
		    }});

	// Force the request to be UTF-8
	LiftRulesJ.
	    j().
	    early().append(Func.lift(new Func1<HTTPRequest, Object>() {
			public Object apply(HTTPRequest req) {
			    req.setCharacterEncoding("UTF-8");
			    return "";
			}
		    }));
	    
	    
	// Use HTML5 for rendering
	LiftRulesJ.j().
	    htmlProperties().
	    theDefault().
	    set(VendorJ.vendor(new Func1<Req, HtmlProperties>() {
		    public HtmlProperties apply(Req r) {
			return new Html5Properties(r.userAgent());
		    }}));
    }
}
