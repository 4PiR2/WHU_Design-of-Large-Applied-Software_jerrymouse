package net.happygod.jerrymouse.server;

import java.io.*;

class FileSender extends Servlet
{
	@Override
	void doDefault(Request request,Response response) throws HTTPException
	{
		String URI=request.getRequestURI();
		File file=new File(getPath(settings(),URI));
		// Check for file permission or not found error.
		if(!file.exists())
		{
			throw new HTTPException(404,"Unable to find "+URI+" on this server");
		}
		if(!file.canRead())
		{
			throw new HTTPException(403,"You have no permission to access "+URI+" on this server");
		}
		if(file.isDirectory())
		{
			if(settings().directory)
			{
				//TODO pretty page
				PrintWriter pw=response.getWriter();
				response.setContentType("text/html; charset=UTF-8");
				pw.println("<html><head></head><body>");
				pw.println("<a href='..'>Parent</a><br />");
				for(File subFile:file.listFiles())
				{
					if(subFile.isDirectory())
						pw.println("<a href='"+subFile.getName()+"/'>"+subFile.getName()+"</a><br />");
					else
						pw.println("<a href='"+subFile.getName()+"'>"+subFile.getName()+"</a><br />");
				}
				pw.println("</body></html>");
				throw new HTTPException(200);
			}
			else
				throw new HTTPException(403,"You have no permission to access "+URI+" on this server");
		}
		else
		{
			String extension="",mime;
			int index=URI.lastIndexOf(".");
			if(index>=0)
				extension=URI.substring(index+1);
			switch(extension)
			{
				case "html":
					mime="text/html; charset=utf-8";
					break;
				case "jpg":
					mime="image/jpeg";
					break;
				case "png":
					mime="image/png";
					break;
				case "gif":
					mime="image/gif";
					break;
				case "css":
					mime="text/css; charset=utf-8";
					break;
				default:
					mime="application/octet-stream";
					//TODO more file types
			}
			response.setContentType(mime);
			try
			{
				BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
				response.setHeader("content-length",file.length()+"");
				response.commit(new HTTPException(200));
				if(!request.getMethod().equals("HEAD"))
					Pipe.pipe(bis,response.getRawStream());
				bis.close();
			}
			catch(IOException ioe)
			{
				throw new HTTPException(500,ioe);
			}
		}
	}
	static String getPath(Settings settings,String URI)
	{
		if(settings==null||settings.path.equals(""))
			return settings.webroot+URI;
		else if(settings.path.charAt(0)!='/')
			return settings.webroot+'/'+settings.path;
		else
			return settings.path;
	}
}
