package net.happygod.jerrymouse.server;

import java.io.*;

class FileManager extends Servlet
{
	@Override
	public void doGet(Request request,Response response) throws HTTPException
	{
		String URI=request.getRequestURI();
		File file=fetchFile(URI);
		if(file.isDirectory())
		{
			if(settings().directory)
			{
				//TODO pretty page & rename function
				PrintWriter pw=response.getWriter();
				response.setContentType("text/html; charset=UTF-8");
				pw.println("<html><head></head><body>");
				pw.println("<form action='' method='POST' enctype='multipart/form-data'>");
				pw.println("<label>Type</label>"
				           +"<br />"
				           +"<input id='file' type='radio' name='type' value='file'>"
				           +"<label for='file'>File</label>"
				           +"<input id='directory' type='radio' name='type' value='directory'>"
				           +"<label for='directory'>Directory</label>"
				           +"<br />");
				pw.println("<label>Operation</label>"
				           +"<br />"
				           +"<input id='create' type='radio' name='operation' value='create'>"
				           +"<label for='create'>Create</label>"
				           +"<input id='rename' type='radio' name='operation' value='rename'>"
				           +"<label for='rename'>Rename</label>"
				           +"<input id='delete' type='radio' name='operation' value='delete'>"
				           +"<label for='delete'>Delete</label>"
				           +"<br />");
				pw.println("<input id='oldname' type='text' name='oldname' placeholder='filename'>"+"<br />");
				pw.println("<label for='file'>Upload File</label>"
				           +"<br />"
				           +"<input id='file' type='file' name='file'>"
				           +"<br />");
				pw.println("<button type='submit'>Submit</button>");
				pw.println("</form>");
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
				if(!"HEAD".equals(request.getMethod()))
					Pipe.pipe(bis,response.getRawStream());
				bis.close();
			}
			catch(IOException ioe)
			{
				throw new HTTPException(500,ioe);
			}
		}
	}
	@Override
	public void doPost(Request request,Response response) throws HTTPException
	{
		File file=fetchFile(request.getRequestURI());
		if(!file.isDirectory())
			throw new HTTPException(404);
		file=new File(file,request.getParameter("oldname"));
		String type=request.getParameter("type"),operation=request.getParameter("operation");
		switch(type)
		{
			case "directory":
				switch(operation)
				{
					case "create":
						file.mkdirs();
						break;
					case "delete":
						file.delete();
						break;
				}
				break;
			case "file":
				switch(operation)
				{
					case "create":
						try
						{
							BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
							bos.write(request.getBinaryParameter("file"));
							bos.close();
						}
						catch(IOException ioe)
						{
							throw new HTTPException(500,ioe);
						}
						break;
					case "delete":
						file.delete();
						break;
				}
				break;
		}
		doGet(request,response);
	}
	private File fetchFile(String URI) throws HTTPException
	{
		File file=new File(Connector.getPath(settings(),URI));
		// Check for file permission or not found error.
		if(!file.exists())
		{
			throw new HTTPException(404,"Unable to find "+URI+" on this server");
		}
		if(!file.canRead())
		{
			throw new HTTPException(403,"You have no permission to access "+URI+" on this server");
		}
		return file;
	}
}
