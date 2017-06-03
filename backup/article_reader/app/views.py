from django.shortcuts import render, redirect
import app.controllers as ac
import json
from django.http import HttpResponseNotFound

# Create your views here.
def home(req) :
	if (req.GET.get('action', "-") == 'reload') :
			ac.reload_all_articles()
			return redirect('home')

	articles = ac.get_all_articles()
	print('selesai load files')

	html_articles = ""

	total_relevan_en = 0
	total_irrelevan_en = 0

	total_relevan_id = 0
	total_irrelevan_id = 0

	total_relevan_id2 = 0
	total_irrelevan_id2 = 0

	for article in articles :
			rel_en = ""
			rel_id_nonstemming = ""
			rel_id_stemming = ""

			if article['rel_en'] == 1:
				rel_en  = 'O'
				total_relevan_en = total_relevan_en + 1
			elif article['rel_en'] == -1 :
				rel_en = 'X'
				total_irrelevan_en = total_irrelevan_en +1
			else :
				rel_en = '-' 

			if article['rel_id_nonstemming'] == 1:
				rel_id_nonstemming  = 'O'
				total_relevan_id = total_relevan_id + 1
			elif article['rel_id_nonstemming'] == -1 :
				rel_id_nonstemming = 'X'
				total_irrelevan_id = total_irrelevan_id +1
			else :
				rel_id_nonstemming = '-' 

			if article['rel_id_stemming'] == 1:
				rel_id_stemming  = 'O'
				total_relevan_id2 = total_relevan_id2 + 1
			elif article['rel_id_stemming'] == -1 :
				rel_id_stemming = 'X'
				total_irrelevan_id2 = total_irrelevan_id2 +1
			else :
				rel_id_stemming = '-' 

			temp = '''
				<tr>
					<td> {0} </td>
					<td> <a href="view/{1}">{2} </a> </td>
					<td> 
						{3}
					</td>
					<td> 
						{4}
					</td>
					<td> 
						{5}
					</td>
				</tr>
			'''.format(article['index'], article['pk'],  article['filename'], rel_en, rel_id_nonstemming, rel_id_stemming)

			html_articles = html_articles + temp

	temp = '''
				<p> total relevan english = {0} </p>  
				<p> total irrelevan english = {1} </p>
				<p> total relevan indonesia non stemming = {4} </p>  
				<p> total irrelevan indonesia non stemming = {5} </p>
				<p> total relevan indonesia stemming = {6} </p>  
				<p> total irrelevan indonesia stemming = {7} </p>
				<p> belum dibaca = {2} </p>
				<p> total artikel = {3} </p>
			'''.format(total_relevan_en, total_irrelevan_en, len(articles) - total_relevan_en - total_irrelevan_en, len(articles), total_relevan_id, total_irrelevan_id, total_relevan_id2, total_irrelevan_id2)

	html_articles = html_articles + temp
	return render(req, 'app/home.html', { 'html_articles' : html_articles })


def view_file(req, fileid) :

	info = None 

	if (req.method == "POST") :
		rel_en = req.POST.get('rel_en', 0)
		rel_id_nonstemming = req.POST.get('rel_id_nonstemming', 0)
		rel_id_stemming = req.POST.get('rel_id_stemming', 0)

		ac.assess_article(pk = fileid, rel_en = rel_en, rel_id_nonstemming = rel_id_nonstemming, rel_id_stemming = rel_id_stemming)
		info = "Anda berhasil menilai artikel ini"

	article = ac.get_file(pk = fileid)

	if (article):
		return render(req, 'app/view_file.html', { 'article' :  article, 'info' : info})
	else :
		return HttpResponseNotFound('<h1>Article not found</h1>')


