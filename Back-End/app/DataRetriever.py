from bs4 import BeautifulSoup
import requests


headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36'
}


# url1 = 'https://vtu.ac.in/en/category/exam-circulars-notifications/'

# url = 'https://vtu.ac.in/en/category/administration-circulars/'


def getData(url):

    page=  requests.get(url, verify = False, headers=headers)
    soup = BeautifulSoup(page.content, 'html.parser')
    content = soup.findAll('article')
    data = []
    for i in content:
        dictionary = {
            'posted_on': i.find(class_='entry-day').get_text() +" "+ i.find(class_ = 'entry-month').get_text(),
            'Content': i.find(class_ = 'content-inner').find('a').get_text(),
            'url': i.find(class_ = 'content-inner').find('a')['href']
        }
        data.append(dictionary)

    return data


