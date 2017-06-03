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

	total_tp_en = 0
	total_fp_en = 0
	total_tn_en = 0
	total_fn_en = 0

	total_tp_id = 0
	total_fp_id = 0
	total_tn_id = 0
	total_fn_id = 0

	for article in articles :
			rel_en = ""
			rel_id = ""
			# rel_id_stemming = ""

			if article['rel_en'] == 1:
				rel_en  = 'TP'
				total_tp_en = total_tp_en + 1
			elif article['rel_en'] == 2 :
				rel_en = 'FP'
				total_fp_en = total_fp_en +1
			elif article['rel_en'] == 3 :
				rel_en = 'TN'
				total_tn_en = total_tn_en +1
			elif article['rel_en'] == 4 :
				rel_en = 'FN'
				total_fn_en = total_fn_en +1
			else :
				rel_en = '-' 

			if article['rel_id'] == 1:
				rel_id  = 'TP'
				total_tp_id = total_tp_id + 1
			elif article['rel_id'] == 2 :
				rel_id = 'FP'
				total_fp_id = total_fp_id +1
			elif article['rel_id'] == 3 :
				rel_id = 'TN'
				total_tn_id = total_tn_id +1
			elif article['rel_id'] == 4 :
				rel_id = 'FN'
				total_fn_id = total_fn_id +1
			else :
				rel_id = '-' 

			# if article['rel_id_stemming'] == 1:
			# 	rel_id_stemming  = 'O'
			# 	total_relevan_id2 = total_relevan_id2 + 1
			# elif article['rel_id_stemming'] == -1 :
			# 	rel_id_stemming = 'X'
			# 	total_irrelevan_id2 = total_irrelevan_id2 +1
			# else :
			# 	rel_id_stemming = '-' 

	# 		temp = '''
	# 			<tr>
	# 				<td> {0} </td>
	# 				<td> <a href="view/{1}">{2} </a> </td>
	# 				<td> 
	# 					{3}
	# 				</td>
	# 				<td> 
	# 					{4}
	# 				</td>
	# 			</tr>
	# 		'''.format(article['index'], article['pk'],  article['filename'], rel_en, rel_id_nonstemming, rel_id_stemming)

	# 		html_articles = html_articles + temp

	# temp = '''
	# 			<p> total relevan english = {0} </p>  
	# 			<p> total irrelevan english = {1} </p>
	# 			<p> total relevan indonesia non stemming = {4} </p>  
	# 			<p> total irrelevan indonesia non stemming = {5} </p>
	# 			<p> total relevan indonesia stemming = {6} </p>  
	# 			<p> total irrelevan indonesia stemming = {7} </p>
	# 			<p> belum dibaca = {2} </p>
	# 			<p> total artikel = {3} </p>
	# 		'''.format(total_relevan_en, total_irrelevan_en, len(articles) - total_relevan_en - total_irrelevan_en, len(articles), total_relevan_id, total_irrelevan_id, total_relevan_id2, total_irrelevan_id2)

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
				</tr>
			'''.format(article['index'], article['pk'],  article['filename'], rel_en, rel_id)

			html_articles = html_articles + temp

	try :
		precision1 = float(total_tp_en) / (total_tp_en + total_fp_en)
	except Exception :
		precision1 = '-';

	try :
		recall1 = float(total_tp_en) / (total_tp_en + total_fn_en)
	except Exception :
		recall1 = '-';

	try :
		precision2 = float(total_tn_en) / (total_tn_en + total_fn_en)
	except Exception :
		precision2 = '-'

	try :
		recall2 = float(total_tn_en) / (total_tn_en + total_fp_en)
	except Exception :
		recall2 = '-';

	try :
		accuracy = float( total_tp_en + total_fn_en) / (total_tp_en + total_fp_en + total_tn_en + total_fn_en)
	except Exception :
		accuracy = '-';

	temp = '''
				<p> total TP english = {0} </p>  
				<p> total FP english = {1} </p>
				<p> total TN english = {2} </p>  
				<p> total FN english = {3} </p>
				<p> total Precision Anies - Sandi	 = {4} </p>
				<p> total Recall Anies - Sandi	 = {5} </p>
				<p> total Precision Ahok - Djarot	 = {4} </p>
				<p> total Recall Ahok - Djarot	 = {5} </p>
				<p> total Accuracy	 = {6} </p>
				<br/>
				<br/>
			'''.format( total_tp_en, total_fp_en, total_tn_en, total_fn_en, precision1, recall1, precision2, recall2, accuracy );


	try :
		precision1 = float(total_tp_id) / (total_tp_id + total_fp_id)
	except Exception :
		precision1 = '-';

	try :
		recall1 = float(total_tp_id) / (total_tp_id + total_fn_id)
	except Exception :
		recall1 = '-';

	try :
		precision2 = float(total_tn_id) / (total_tn_id + total_fn_id)
	except Exception :
		precision2 = '-'

	try :
		recall2 = float(total_tn_id) / (total_tn_id + total_fp_id)
	except Exception :
		recall2 = '-';

	try :
		accuracy = float( total_tp_id + total_fn_id) / (total_tp_id + total_fp_id + total_tn_id + total_fn_id)
	except Exception :
		accuracy = '-';

	temp = temp + '''
				<p> total TP indonesia = {0} </p>  
				<p> total FP indonesia = {1} </p>
				<p> total TN indonesia = {2} </p>  
				<p> total FN indonesia = {3} </p>
				<p> total Precision Anies - Sandi	 = {4} </p>
				<p> total Recall Anies - Sandi	 = {5} </p>
				<p> total Precision Ahok - Djarot	 = {4} </p>
				<p> total Recall Ahok - Djarot	 = {5} </p>
				<p> total Accuracy	 = {6} </p>
				<br/>
				<br/>
			'''.format(total_tp_id, total_fp_id, total_tn_id, total_fn_id, precision1, recall1, precision2, recall2, accuracy );

	total = len(articles)
	temp = temp + '''
				<p> belum dibaca = {0} </p>
				<p> total artikel = {1} </p>
			'''.format(total - (total_tp_id + total_fp_id + total_tn_id + total_fn_id), total)

	html_articles = html_articles + temp
	return render(req, 'app/home.html', { 'html_articles' : html_articles })


def view_file(req, fileid) :

	info = None 

	if (req.method == "POST") :
		rel = req.POST.get('rel', '-')

		ac.assess_article(pk = fileid, rel=rel)
		info = "Anda berhasil menilai artikel ini"

	article = ac.get_file(pk = fileid)

	if (article):
		return render(req, 'app/view_file.html', { 'article' :  article, 'info' : info})
	else :
		return HttpResponseNotFound('<h1>Article not found</h1>')


