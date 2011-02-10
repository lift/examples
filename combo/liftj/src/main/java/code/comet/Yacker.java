package code.comet;

import net.liftweb.util.*;
import net.liftweb.http.*;
import java.util.Date;

public class Yacker extends CometActorJ {
    public void localSetup() {
	ping();
	super.localSetup();
    }

    private void ping() {
	(new ScheduleJBridge()).schedule().perform(this,
						   new Pinger(),
						   10000);
    }

    @Receive protected void ping(Pinger p) {
	ping();
	reRender();
    }
	
    public RenderOut render() {
	return nsToNsFuncToRenderOut(Css.sel("#yack", 
					     (new Date()).toString()));
    }

    class Pinger {
    }
}

