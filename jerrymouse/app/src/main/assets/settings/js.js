var table=tableInfo.table;
function select(button)
{
	$.ajax
	({
		type:'GET',
		dataType:'json',
		url:'./execute',
		data:button.parents('form').serialize(),
		success:function(data)
		{
			data=eval(data);
			if(data.operation==null)
			{
				errorTip(data.message);
				return;
			}
			if(button.attr('class')=='select')
				$('div#showTable').html('');
			data.values.forEach(function(result)
			{
				var resultForm=
					"<form class='tr'>\
					<input type='hidden' name='table' value='"+table+"'>";
				for(column in tableInfo.columns)
				{
					var columnInfo=tableInfo.columns[column];
					var key=columnInfo.key;
					if(key=='')
						continue;
					var value=result[key]!=null?result[key]:'';
					if(columnInfo.type=='view')
						resultForm+="<input class='td' type='text' value='"+value+"' disabled>";
					else
						resultForm+=
							"<input type='hidden' name='"+"p"+key+"' value='"+value+"'>\
							<input class='td' type='text' name='"+key+"' value='"+value+"' list='"+key+"' oninput='updateable($(this))'>";
				}
				resultForm+=
					"<div class='td tr'>\
					<div class='td tb'>\
					<button class='update' type='button' onclick='update($(this));' disabled>Update</button>\
					</div>\
					<div class='td tb'>\
					<button class='delete' type='button' onclick='update($(this));'>Delete</button>\
					</div>\
					</div>\
					</form>";
				switch(button.attr('class'))
				{
					case 'select':
						$('div#showTable').append(resultForm);
						break;
					case 'insert':
						$('div#showTable').prepend(resultForm);
						break;
					case 'update':
						button.parents('form').replaceWith(resultForm);
						break;
				}
			});
			if(data.rows==0)
				alert('None records matched!');
		},
		error:function(){errorTip('Offline!');}
	});
	$(window).resize();
}
function update(button)
{
	switch(button.attr('class'))
	{
		case 'delete':
			button.parents('form').find('input:text').val('');
		case 'insert':
		case 'update':
			$.ajax
			({
				type:'POST',
				dataType:'json',
				url:'./execute',
				data:button.parents('form').serialize(),
				success:function(data)
				{
					switch(data.operation)
					{
						case null:
							errorTip(data.message);
							return;
						case 'insert':
						case 'update':
							button.parents('form').prepend("<input type='hidden' name='tablea'>");
							select(button);
							$("input:hidden[name='tablea']").remove();
							return;
						case 'delete':
							button.parents('form').remove();
							return;
					}
				},
				error:function(){errorTip('Offline!');}
			});
	}
}
function errorTip(message)
{
	var tip='Error!\n'+message;
	if(message=='Offline!')
		tip+='\nPlease check your network connection and try again!';
	else
		tip+='\nPlease check your parameters and try again!';
	alert(tip);
}
function updateable(input)
{
	var flag=false;
	var form=input.parents('form');
	for(column in tableInfo.columns)
	{
		var columnInfo=tableInfo.columns[column];
		var key=columnInfo.key;
		if(form.find("input:text[name='"+key+"']").val()!=form.find("input:hidden[name='p"+key+"']").val())
			flag=true;
		if(columnInfo.type=='primary'&&form.find("input:text[name='"+key+"']").val()=='')
		{
			flag=false;
			break;
		}
	}
	if(flag)
		form.find('button.update').removeAttr('disabled');
	else
		form.find('button.update').attr('disabled',true);
}
function insertable(input)
{
	var flag=true;
	var form=input.parents('form');
	for(column in tableInfo.columns)
	{
		var columnInfo=tableInfo.columns[column];
		switch(columnInfo.type)
		{
			case 'primary':
				if(form.find("input:text[name='"+columnInfo.key+"']").val()=='')
					flag=false;
				break;
			case 'view':
				if(form.find("input:text[name='"+columnInfo.key+"']").val()!='')
					flag=false;
				break;
		}
		if(!flag)
			break;
	}
	if(flag)
		$('button.insert').removeAttr('disabled');
	else
		$('button.insert').attr('disabled',true);
}
$(window).resize(function()
{
	$('div#blankTable').css('height',$('div#headTable').css('height'));
});
document.title+=' '+table.substring(0,1).toUpperCase()+table.substring(1).toLowerCase();
$('h2').html(table.toUpperCase());
var headline='';
for(column in tableInfo.columns)
{
	switch(tableInfo.columns[column].type)
	{
		case 'primary':
			headline+="<span class='td'>"+column+'*'+"</span>";
			break;
		case 'hidden':
			break;
		default:
			headline+="<span class='td'>"+column+"</span>";
	}
}
$('div#headline').html(headline);
var selectForm=
	"<form class='tr'>\
	<input type='hidden' name='table' value='"+table+"'>";
for(column in tableInfo.columns)
{
	var key=tableInfo.columns[column].key;
	if(tableInfo.columns[column].type=='hidden'||tableInfo.columns[column].type=='label')
		continue;
	selectForm+="<input class='td' type='text' name='"+key+"' value='' list='"+key+"' oninput='insertable($(this));'>";
}
selectForm+=
	"<div class='td tr'>\
	<div class='td tb'>\
	<button class='select' type='button' onclick='select($(this));'>Search</button>\
	</div>\
	<div class='td tb'>\
	<button class='insert' type='button' onclick='update($(this));' disabled>Insert</button>\
	</div>\
	</div>\
	</form>";
$('hr').before(selectForm);
$(window).resize();
