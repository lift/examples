package code.snippet;

import net.liftweb.util.*;

import scala.xml.NodeSeq;

public class JavaSnippet {
    public CssSel render() {
	return Css.sel("#cow", "big").
	    sel("#pig", "oink");
    }
}