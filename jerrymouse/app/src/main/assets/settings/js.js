var currententry=null;
var cleannullvalue=function(data)
{
	data['values'].forEach(entry => {
		for(var key in entry)
		{
			if(entry[key]==null)
			{
				data['values'][key]='';
			}
		}
	});
	return data;
};
var sendform=function(form,funs,fune)
{
	$.ajax
	({
		type:form.attr('method'),
		dataType:'json',
		url:form.attr('action'),
		data:form.serialize(),
		success:funs,
		error:fune
	});
};
var sendsql=function(sql,funs,fune)
{
	$('#sql').val(sql);
	sendform($('#dbsql'),funs,fune);
};
var closewindow=function()
{
	$('#floatingwindow').css('display','none');
	currententry=null;
}
var errortip=function()
{
	alert('Wrong Parameters!\nPlease check your settings and try again!');
}
