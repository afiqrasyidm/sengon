from django.conf.urls import url
import app.views  as av

urlpatterns = [
    url(r'^$', av.home, name='home'),
    url(r'^view/(?P<fileid>[0-9]+)$', av.view_file, name='view_file')
]
