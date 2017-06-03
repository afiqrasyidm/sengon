from django.db import models
from django.utils import timezone


# Create your models here.
class Article(models.Model) :
	filename = models.CharField(max_length=128)
	article = models.FileField(upload_to='articles_politik/')
	upload_at = models.DateTimeField(auto_now_add=True)

	def __str__(self):
		return str(self.id) + " " + self.filename

class EnglishSentiment(models.Model) :
	article = models.ForeignKey(Article, on_delete=models.CASCADE)
	result = models.CharField(max_length=128)
	value = models.IntegerField(default = 0)
	created_at = models.DateTimeField(default=timezone.now, blank=True)
	updated_at = models.DateTimeField(default=timezone.now, blank=True)

	def __str__(self):
		return str(self.id) + " " + self.article.filename

class IndonesiaSentiment(models.Model) :
	article = models.ForeignKey(Article, on_delete=models.CASCADE)
	result = models.CharField(max_length=128)
	value = models.IntegerField(default = 0)
	created_at = models.DateTimeField(default=timezone.now, blank=True)
	updated_at = models.DateTimeField(default=timezone.now, blank=True)

	def __str__(self):
		return str(self.id) + " " + self.article.filename

# class IndonesiaSentimentStemming(models.Model) :
# 	article = models.ForeignKey(Article, on_delete=models.CASCADE)
# 	result = models.FileField(upload_to='sentiment_en_articles_politik/')
# 	value = models.IntegerField(default = 0)
# 	created_at = models.DateTimeField(default=timezone.now, blank=True)
# 	updated_at = models.DateTimeField(default=timezone.now, blank=True)
	
# 	def __str__(self):
# 		return str(self.id) + " " + self.article.filename


	
