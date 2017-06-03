from django.contrib import admin
import app.models as models


# Register your models here.
admin.site.register(models.Article)
admin.site.register(models.EnglishSentiment)
admin.site.register(models.IndonesiaSentimentNonStemming)
admin.site.register(models.IndonesiaSentimentStemming)