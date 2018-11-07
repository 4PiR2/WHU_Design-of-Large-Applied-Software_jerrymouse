<%@ page pageEncoding="UTF-8" %>
<html>
	<head>
		<title>EMS</title>
		<link rel='shortcut icon' href='./icon.ico'/>
		<link rel='stylesheet' href='./css.css' type='text/css'/>
		<script src='./jquery-min.js'></script>
		<script type='text/javascript'>
			var tableInfo=
			{
			<%
			String pageType=request.getParameter("page");
			%>
				'table':<%=pageType!=null?"'"+pageType+"'":"''"%>,
				'columns':
				{
			<%
			if(pageType!=null)
			{
				switch(pageType)
				{
					case "school":
			%>
					'School':{'key':'school','type':'primary','foreign':false},
					'Classes':{'key':'class','type':'view','foreign':false},
					'Students':{'key':'student','type':'view','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					case "class":
			%>
					'Class':{'key':'class','type':'primary','foreign':false},
					'School':{'key':'school','type':'primary','foreign':true},
					'Students':{'key':'student','type':'view','foreign':false},
					'Average Score':{'key':'score','type':'view','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					case "student":
			%>
					'ID':{'key':'sid','type':'primary','foreign':false},
					'Name':{'key':'student','type':'field','foreign':false},
					'School':{'key':'school','type':'field','foreign':true},
					'Class':{'key':'class','type':'field','foreign':true},
					'Gender':{'key':'gender','type':'field','foreign':false},
					'Telephone':{'key':'tel','type':'field','foreign':false},
					'Credits':{'key':'credit','type':'view','foreign':false},
					'Average Score':{'key':'score','type':'view','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					case "teacher":
			%>
					'ID':{'key':'tid','type':'primary','foreign':false},
					'Name':{'key':'teacher','type':'field','foreign':false},
					'School':{'key':'school','type':'field','foreign':true},
					'Gender':{'key':'gender','type':'field','foreign':false},
					'Rank':{'key':'rank','type':'field','foreign':false},
					'Working Years':{'key':'seniority','type':'field','foreign':false},
					'Courses':{'key':'course','type':'view','foreign':false},
					'Students':{'key':'student','type':'view','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					case "course":
			%>
					'ID':{'key':'cid','type':'primary','foreign':false},
					'Name':{'key':'course','type':'field','foreign':false},
					'Credit':{'key':'credit','type':'field','foreign':false},
					'Teacher ID':{'key':'tid','type':'field','foreign':true},
					'Teacher Name':{'key':'teacher','type':'view','foreign':false},
					//'Teacher Rank':{'key':'rank','type':'view','foreign':false},
					'School':{'key':'school','type':'view','foreign':false},
					'Students':{'key':'student','type':'view','foreign':false},
					'Average Score':{'key':'score','type':'view','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					case "score":
			%>
					'Course ID':{'key':'cid','type':'primary','foreign':true},
					'Course Name':{'key':'course','type':'view','foreign':false},
					'Course School':{'key':'cschool','type':'view','foreign':false},
					'Teacher Name':{'key':'teacher','type':'view','foreign':false},
					//'Teacher Rank':{'key':'rank','type':'view','foreign':false},
					'Student ID':{'key':'sid','type':'primary','foreign':true},
					'Student Name':{'key':'student','type':'view','foreign':false},
					'Student School':{'key':'sschool','type':'view','foreign':false},
					'Score':{'key':'score','type':'field','foreign':false},
					'Operation':{'key':'','type':'','foreign':false}
			<%
						break;
					default:
						pageType=null;
						break;
				}
			}
			%>
				}
			};
		</script>
	</head>
	<body>
		<div id='headTable'>
			<h2>EDUCATIONAL MANAGEMENT SYSTEM</h2>
			<div id='links' class='tr'>
				<h3 class='td'><a href='?'>Main Page</a></h3>
				<h3 class='td'><a href='?page=school'>School</a></h3>
				<h3 class='td'><a href='?page=class'>Class</a></h3>
				<h3 class='td'><a href='?page=student'>Student</a></h3>
				<h3 class='td'><a href='?page=teacher'>Teacher</a></h3>
				<h3 class='td'><a href='?page=course'>Course</a></h3>
				<h3 class='td'><a href='?page=score'>Score</a></h3>
			</div>
			<div id='headline' class='tr'>
				<div class='td'>
					<span>Welcome to Educational Management System! Please choose one object above to continue! ↑↑↑</span>
					<br/>
					<span>© 2018 陈嘉乐</span>
				</div>
			</div>
			<hr/>
		</div>
		<%if(pageType!=null){%>
		<div id='blankTable'></div>
		<div id='showTable'></div>
		<datalist id='rank'>
			<option value='Professor'/>
			<option value='Associate professor'/>
			<option value='Lecturer'/>
			<option value='Assistant lecturer'/>
		</datalist>
		<datalist id='gender'>
			<option value='Male'/>
			<option value='Female'/>
		</datalist>
		<script src='./js.js'></script>
		<%}%>
	</body>
</html>
