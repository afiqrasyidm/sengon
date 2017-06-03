import app.models as models
import json
import os
from django.conf import settings

def reload_all_articles() :
	models.Article.objects.all().delete()
	models.EnglishSentiment.objects.all().delete()
	models.IndonesiaSentiment.objects.all().delete()

	files = os.listdir(os.path.join(settings.STATIC_ROOT, "app/articles_politik"))
	length = len(files)
	path_en = os.path.join(settings.STATIC_ROOT, "app/sentiment_en_articles_politik")
	path_id = os.path.join(settings.STATIC_ROOT, "app/sentiment_id_articles_politik")
	en_files = os.listdir(path_en)
	id_files = os.listdir(path_id)

	for i in range(length) :
		print(i)
		article = files[i]
		f = models.Article.objects.create(filename=article, article=article)
		
		article = open( os.path.join(path_en, en_files[i]), 'br');
		datajson = json.loads(article.readlines()[0].decode("utf-8", "ignore"))
		models.EnglishSentiment.objects.create(article=f, result=datajson["partySupport"])

		article = open( os.path.join(path_id, id_files[i]), 'br');
		datajson = json.loads(article.readlines()[0].decode("utf-8", "ignore"))
		models.IndonesiaSentiment.objects.create(article=f, result=datajson["partySupport"])
		

def assess_article(pk, rel) :
	try :
		article = models.Article.objects.get(pk = pk)

		articles = models.EnglishSentiment.objects.filter(article = article)
		for a in articles :
			if (a.result == "Anies - Sandi") :
				if (rel == "Anies - Sandi") :
					a.value = 1
				else :
					a.value = 2
			else :
				if (rel == "Ahok - Djarot") :
					a.value = 3
				else :
					a.value = 4
			a.save()

		articles = models.IndonesiaSentiment.objects.filter(article = article)
		for a in articles :
			if (a.result == "Anies - Sandi") :
				if (rel == "Anies - Sandi") :
					a.value = 1
				else :
					a.value = 2
			else :
				if (rel == "Ahok - Djarot") :
					a.value = 3
				else :
					a.value = 4
			a.save()
		
	except models.Article.DoesNotExist :
		pass
		
def get_all_articles() :
	# 1 True Positive, Predicted Anies - Sandi 	Actual : Anies - Sandi
	# 2 False Positive, Predicted Anies - Sandi 	Actual : Ahok - Djarot
	# 3 True Negative, Predicted Ahok - Djarot 	Actual : Ahok - Djarot
	# 4 False Negative, Predicted Ahok - Djarot 	Actual : Anies - Sandi
	
	articles = models.Article.objects.all()
	result = []
	i = 1
	print("get files")
	for article in articles :
		print(article.pk)
		temp = {}
		temp['filename'] = article.filename
		temp['pk'] = article.pk
		temp['index'] = i
		
		try :
			temp['rel_en'] = models.EnglishSentiment.objects.get(article = article).value
		except models.EnglishSentiment.DoesNotExist :
			temp['rel_en'] = 0

		try :
			temp['rel_id'] = models.IndonesiaSentiment.objects.get(article = article).value
		except models.IndonesiaSentiment.DoesNotExist :
			temp['rel_id'] = 0

		# try :
		# 	temp['rel_id_stemming'] = models.IndonesiaSentimentStemming.objects.get(article = article).value
		# except models.IndonesiaSentimentStemming.DoesNotExist :
		# 	temp['rel_id_stemming'] = 0

		i = i + 1

		result.append(temp)

	return result


def get_file(pk) :
	result = {}

	try :
		article = models.Article.objects.get(pk = pk)
		path  = os.path.join(settings.STATIC_ROOT, "app/articles_politik")
		f = open(os.path.join(path, str(article.article)), 'br')
		datajson = json.loads(f.readlines()[0].decode("utf-8", "ignore"))
		result['title'] = datajson['title']
		result['section_titles']  = datajson['section_titles']
		result['paragraphs'] = datajson['paragraphs']

	except models.Article.DoesNotExist :
		return None

	try :
		sentiment = models.EnglishSentiment.objects.get(article = article)
		result['rel_en'] = sentiment.result

	except models.EnglishSentiment.DoesNotExist:
		return None

	try :
		sentiment = models.IndonesiaSentiment.objects.get(article = article)
		result['rel_id'] = sentiment.result

	except models.IndonesiaSentiment.DoesNotExist:
		return None

	return result