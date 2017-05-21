import app.models as models
import json
import os
from django.conf import settings

def reload_all_articles() :
	models.Article.objects.all().delete()
	models.EnglishSentiment.objects.all().delete()
	models.IndonesiaSentimentNonStemming.objects.all().delete()
	models.IndonesiaSentimentStemming.objects.all().delete()

	files = os.listdir(os.path.join(settings.STATIC_ROOT, "app/articles_politik"))
	length = len(files)
	en_files = os.listdir(os.path.join(settings.STATIC_ROOT, "app/sentiment_en_articles_politik"))
	id_nonstemming_files = os.listdir(os.path.join(settings.STATIC_ROOT, "app/sentiment_id_articles_politik"))
	id_stemming_files = os.listdir(os.path.join(settings.STATIC_ROOT, "app/sentiment_stemming_id_articles_politik"))

	for i in range(length) :
		print(i)
		article = files[i]
		f = models.Article.objects.create(filename=article, article=article)
		article = en_files[i]
		models.EnglishSentiment.objects.create(article=f, result=article)
		article = id_nonstemming_files[i]
		models.IndonesiaSentimentNonStemming.objects.create(article=f, result=article)
		article = id_stemming_files[i]
		models.IndonesiaSentimentStemming.objects.create(article=f, result=article)
		

def assess_article(pk, rel_en, rel_id_nonstemming, rel_id_stemming) :
	try :
		article = models.Article.objects.get(pk = pk)
		models.EnglishSentiment.objects.filter(article = article).update(value=rel_en)
		models.IndonesiaSentimentNonStemming.objects.filter(article = article).update(value=rel_id_nonstemming)
		models.IndonesiaSentimentStemming.objects.filter(article = article).update(value=rel_id_stemming)
		
	except models.Article.DoesNotExist :
		pass
		
def get_all_articles() :
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
			temp['rel_id_nonstemming'] = models.IndonesiaSentimentNonStemming.objects.get(article = article).value
		except models.IndonesiaSentimentNonStemming.DoesNotExist :
			temp['rel_id_nonstemming'] = 0

		try :
			temp['rel_id_stemming'] = models.IndonesiaSentimentStemming.objects.get(article = article).value
		except models.IndonesiaSentimentStemming.DoesNotExist :
			temp['rel_id_stemming'] = 0

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
		path  = os.path.join(settings.STATIC_ROOT, "app/sentiment_en_articles_politik")
		f = open(os.path.join(path, str(sentiment.result)), 'br')
		datajson = json.loads(f.readlines()[0].decode("utf-8", "ignore"))
		result['rel_en'] = []
		num = min(5, len(datajson))
		for r in range(num) :
			result['rel_en'].append(datajson[r]['name'])

	except models.EnglishSentiment.DoesNotExist:
		return None

	try :
		sentiment = models.IndonesiaSentimentNonStemming.objects.get(article = article)
		path  = os.path.join(settings.STATIC_ROOT, "app/sentiment_id_articles_politik")
		f = open(os.path.join(path, str(sentiment.result)), 'br')
		datajson = json.loads(f.readlines()[0].decode("utf-8", "ignore"))
		result['rel_id_nonstemming'] = []
		num = min(5, len(datajson))
		for r in range(num) :
			result['rel_id_nonstemming'].append(datajson[r]['name'])
	except models.IndonesiaSentimentNonStemming.DoesNotExist:
		return None

	try :
		sentiment = models.IndonesiaSentimentStemming.objects.get(article = article)
		path  = os.path.join(settings.STATIC_ROOT, "app/sentiment_stemming_id_articles_politik")
		f = open(os.path.join(path, str(sentiment.result)), 'br')
		datajson = json.loads(f.readlines()[0].decode("utf-8", "ignore"))
		result['rel_id_stemming'] = []
		num = min(5, len(datajson))
		for r in range(num) :
			result['rel_id_stemming'].append(datajson[r]['name'])

	except models.IndonesiaSentimentStemming.DoesNotExist:
		return None


	return result