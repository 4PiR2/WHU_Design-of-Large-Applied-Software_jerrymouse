import net.happygod.jerrymouse.server.*;

import java.io.*;
import java.util.*;

public class divide0 extends Servlet
{
	@Override
	public void doGet(final Request request,final Response response)
	{
		int i=1/0;
	}

	@Override
	public void doPost(Request request,Response response)
	{
		doGet(request,response);
	}
}
