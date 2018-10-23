package net.happygod.jerrymouse.server;

import android.os.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

class FileManager extends Servlet
{
	@Override
	public void doGet(Request request,Response response) throws HTTPException
	{
		String URI=request.getRequestURI();
		File file=new File(settings().path);
		checkFile(file,"r");
		if(file.isDirectory())
		{
			if(settings().directory)
			{
				//TODO pretty page
				PrintWriter pw=response.getWriter();
				response.setContentType("text/html; charset=UTF-8");
				pw.println("<html><head><meta content='text/html;charset=utf-8'><title>File Manager</title></head><body>");
				pw.println("<form action='' method='POST' enctype='multipart/form-data'>");
				pw.println("<label>Operation</label>"
				           +"<br />"
				           +"<input id='create' type='radio' name='operation' value='create'>"
				           +"<label for='create'>Create</label>"
				           +"<input id='rename' type='radio' name='operation' value='rename'>"
				           +"<label for='rename'>Rename</label>"
				           +"<input id='delete' type='radio' name='operation' value='delete'>"
				           +"<label for='delete'>Delete</label>"
				           +"<br />");
				pw.println("<label>Type</label>"
				           +"<br />"
				           +"<input id='filetype' type='radio' name='type' value='file'>"
				           +"<label for='filetype'>File</label>"
				           +"<input id='directorytype' type='radio' name='type' value='directory'>"
				           +"<label for='directorytype'>Directory</label>"
				           +"<br />");
				pw.println("<label>Filename</label>"
				           +"<br />"
				           +"<label for='oldname'>Old</label>"
				           +"<input id='oldname' type='text' name='oldname'>"
				           +"<label for='newname'>New</label>"
				           +"<input id='newname' type='text' name='newname'>"
				           +"<br />");
				pw.println("<label for='file'>Upload File</label>"
				           +"<br />"
				           +"<input id='file' type='file' name='file' onchange=\"document.getElementById('newname').value=this.value.substring(Math.max(this.value.lastIndexOf('\\\\'),this.value.lastIndexOf('/'))+1)\">"
				           +"<br />");
				pw.println("<button type='submit'>Submit</button>");
				pw.println("</form><table>");
				pw.println("<tr><td><a href='..'>Parent Directory</a></td></tr>");
				for(File subFile:file.listFiles())
				{
					pw.println("<tr><td><a href='"+subFile.getName()+(subFile.isFile()?"' target='_blank'":"/' ")+">"+subFile.getName()+"</a></td><td>"+new Date(subFile.lastModified())+"</td><td>"+subFile.length()+"</td></tr>");
				}
				pw.println("</table></body></html>");
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
		File file=new File(settings().path);
		checkFile(file,"r");
		if(file.isDirectory())
		{
			File oldfile=new File(file,request.getParameter("oldname"));
			checkFile(oldfile,"w");
			File newfile=new File(file,request.getParameter("newname"));
			checkFile(newfile,"");
			boolean success=false;
			String type=request.getParameter("type"), operation=request.getParameter("operation");
			switch(operation)
			{
				case "create":
					if("file".equals(type))
					{
						try
						{
							BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(newfile));
							bos.write(request.getBinaryParameter("file"));
							bos.close();
							success=true;
						}
						catch(IOException ioe)
						{
							throw new HTTPException(500,ioe);
						}
					}
					else if("directory".equals(type))
					{
						success=newfile.mkdirs();
					}
					break;
				case "rename":
					success=oldfile.renameTo(newfile);
					break;
				case "delete":
					success=deleteR(oldfile);
					break;
			}
			if(!success)
				throw new HTTPException(500);
		}
		doGet(request,response);
	}
	private void checkFile(File file,String mode) throws HTTPException
	{
		// Check for file permission or not found error.
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		if(!Paths.get(file.getAbsolutePath()).normalize().startsWith(Paths.get(new File(settings().webroot).getAbsolutePath()).normalize())&&settings().port!=1998)
		{
			throw new HTTPException(403,"You have no permission to access "+file.getName()+" on this server");
		}
		if((mode.contains("e")||mode.contains("r")||mode.contains("w"))&&!file.exists())
		{
			throw new HTTPException(404,"Unable to find "+file.getName()+" on this server");
		}
		if(mode.contains("r")&&!file.canRead()||mode.contains("w")&&!file.canWrite())
		{
			throw new HTTPException(403,"You have no permission to access "+file.getName()+" on this server");
		}
	}
	private boolean deleteR(File file)
	{
		if(file.isDirectory())
		{
			for(File subFile:file.listFiles())
			{
				deleteR(subFile);
			}
		}
		return file.delete();
	}
}
