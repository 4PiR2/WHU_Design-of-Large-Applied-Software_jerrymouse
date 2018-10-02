import os

def list_all_files(rootdir):
	_files = []
	list = os.listdir(rootdir)
	for i in range(0,len(list)):
		path = os.path.join(rootdir,list[i])
		if os.path.isdir(path):
			_files.extend(list_all_files(path))
		if os.path.isfile(path):
			_files.append(path)
	return _files

def process(name):
	count=0
	txtfile=open(name,'r')
	for line in txtfile:
		count+=1
	txtfile.close()
	return count

fs=list_all_files('.\\jerrymouse\\app\\src')
count=0
for name in fs:
	print(name)
	try:
		d=process(name)
	except:
		print('b')
	print(d)
	count+=d
print(count)
input()
