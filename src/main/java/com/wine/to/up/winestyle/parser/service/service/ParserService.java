package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.dto.WineDto;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class ParserService implements IParserService {
    private final IWineService wineService;
    private final IDocumentService documentService;
    private volatile Boolean iAmUsed = false;

    String mainUrl = "https://spb.winestyle.ru";
    String wineUrl = "/wine/wines_ll/";

    public ParserService(IWineService wineService, IDocumentService documentService) {
        this.wineService = wineService;
        this.documentService = documentService;
    }

    // Запуск парсинга
    public void startParsingJob(String alcoholType) throws ServiceIsBusyException {
        if (!this.iAmUsed) {
            if (alcoholType.equals("wine")) {
                Thread newThread = new Thread(() -> {
                    try {
                        parseByPages(wineUrl);
                    } catch (InterruptedException e) {
                        log.error("Thread is sleeping!", e);
                    }
                });
                newThread.start();
            }
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running.");
        }
    }

    //At 00:00; every day
    @Scheduled(cron = "${cron.expression}") // second, minute, hour, day of month, month, day(s) of week(0-6)
    public void onScheduleParseWinePages() throws ServiceIsBusyException {
        if (!iAmUsed) {
            try {
                parseByPages(wineUrl);
            } catch (InterruptedException e) {
                log.error("Error on schedule with parsing wines pages!", e);
            }
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running.");
        }
    }

    // Пробегаем по страничкам интернет-магазина
    private void parseByPages(String relativeUrl) throws InterruptedException {
        this.iAmUsed = true;
        Document parsePageDoc = documentService.getJsoupDocument(mainUrl + relativeUrl);

        Document productDoc;

        int pages = documentService.pagesNumber(parsePageDoc);

        for (int i = 2; i <= pages; i++) {
            Elements wineElements = parsePageDoc.getElementsByClass("item-block-content");

            for (Element infoBlock : wineElements) {
                Elements els = infoBlock.getElementsByClass("title");
                for (Element el : els) {
                    String urlToProductPage = el.getElementsByAttribute("href").toString();
                    urlToProductPage = urlToProductPage.
                            substring(urlToProductPage.
                                    indexOf("<a href=\"") + 9, urlToProductPage.indexOf("\">"));
                    productDoc = documentService.getJsoupDocument(mainUrl + urlToProductPage);
                    if (wineService.getWineByUrl(urlToProductPage) == null){
                        parsePage(productDoc, urlToProductPage);
                    } else {
                        parseUpdatePrice(infoBlock, urlToProductPage);
                    }
                }
            } //Переход на страницу продукции
            parsePageDoc = documentService.getJsoupDocument(mainUrl + relativeUrl + "?page=" + i);
        }
    }

    //обновление позиции
    private void parseUpdatePrice(Element infoBlock, String url){
        Elements elems = infoBlock.getElementsByClass("price");
        String priceForUpdate = elems.text();
        priceForUpdate = priceForUpdate.replaceAll("руб\\.", "").replaceAll(" ", "");
        wineService.updatePrice(priceForUpdate, url);
    }

    // основной парсинг
    private void parsePage(Document doc, String urlToProductPage) {
        String price = "noPrice";
        String name = "noName";
        String urlImage = "noUrlImage";
        String tastingNotes = "noTastingNotes";
        ArrayList<String> values;

        //Название
        Elements mainHeader = doc.getElementsByClass("main-header");

        //вино, регион, производитель, бренд, крепость, объем, виноград
        Elements mainInfo = doc.getElementsByClass("main-info");

        Element aImage = doc.select("a[itemprop=image]").first();
        urlImage = aImage.getElementsByAttribute("href").toString();
        urlImage = urlImage.
                substring(urlImage.
                        indexOf("\" href=\"") + 8, urlImage.indexOf("\" title"));

        boolean checkStock = true;
        for (Element el: mainInfo){
            //Товар в наличии или нет, если нет, то price надо по-другому парсить (из main-info, а не right-info)
            if (el.getElementsByClass("stock").hasText()) checkStock = false;
        }
        // цена
        Elements rightInfo = doc.getElementsByClass("right-info");

        Elements tastingInfo = doc.getElementsByClass("item-content item-content_second item-content_no-js");
        tastingNotes = parseTastingInfo(tastingInfo);

        name = parseHeader(mainHeader);
        values = new ArrayList<>(parseMainInfo(mainInfo));

        //TODO: Double.parseDouble(price)
        if (checkStock){
            price = parsePrice(rightInfo);
        } else {
            price = parsePriceFromMainInfo(mainInfo);
        }

        createWine(name, price, values, urlToProductPage, urlImage, tastingNotes);
    }

    /**
     *
     * @param header
     * @return wine name
     */
    private String parseHeader(Elements header){
        String name = "noHeaderName";
        for (Element head: header){
            name = head.getElementsByClass("text").toString();
            name = name.substring(name.indexOf("<span class=\"text\">")+19, name.lastIndexOf(",")); //TODO: имя вина
        }
        return name;
    }

    /**
     *
     * @param rightInfo
     * @return String price
     */
    private String parsePrice(Elements rightInfo){
        String price = "noPrice";
        for (Element el: rightInfo){
            price = el.getElementsByClass("price").toString();
            price = price.substring(price.indexOf("<div class=\"price \">")+22, price.indexOf("<span>")-1);
        }
        return price;
    }

    private String parsePriceFromMainInfo(Elements mainInfo){
        String price = "noPrice_noStock";
        for (Element el: mainInfo){
            price = el.getElementsByClass("price").text();
        }
        return price;
    }

    private ArrayList<String> parseMainInfo(Elements info){
        ArrayList<String> arrInfo = new ArrayList<>();
        for (Element el : info) {
            Elements liElements = el.getElementsByTag("li");
            for (Element li : liElements) {
                if (li.text().length() == 0) continue;
                arrInfo.add(li.text());
            }

            Element metaYear = el.select("meta[itemprop=releaseDate]").first();
            String cropYear;

            if (metaYear != null) {
                cropYear = metaYear.attr("content");
            } else {
                cropYear = "-1";
                log.warn("product's crop year is not specified");
            }

            arrInfo.add("Год: " + cropYear);

            try {
                Element metaRatingValue = el.select("meta[itemprop=ratingValue]").first();
                String ratingValue = metaRatingValue.attr("content");
                arrInfo.add("Рейтинг: " + Double.parseDouble(ratingValue) / 2.);
            } catch (Exception ex) {
                log.warn("product has no winestyle's rating");
            }
        }
        return arrInfo;
    }

    private String parseTastingInfo(Elements tastingInfo){
        String colorDescription = "noColorDescription";
        String aromaDescription = "noAromaDescription";
        String tasteDescription = "noTasteDescription";
        String gastronomicDescription = "noGastronomicDescription";

        for (Element el: tastingInfo){
            Elements elements = el.getElementsByClass("description-block");
            for (Element description: elements){
                if (description.text().contains("Цвет")){
                    colorDescription = description.text();
                }
                if (description.text().contains("Аромат")){
                    aromaDescription = description.text();
                }
                if (description.text().contains("Вкус")){
                    tasteDescription = description.text();
                }
                if (description.text().contains("Гастроном")){
                    gastronomicDescription = description.text();
                }
            }
        }
        return colorDescription + " " + aromaDescription + " " + tasteDescription + " " + gastronomicDescription;
    }

    private Wine createWine(String name, String price, ArrayList<String> values,
                            String urlToProductPage, String urlImage, String tastingNotes){
        WineDto wineDto = new WineDto();
        if (name.contains(","))
            name = name.substring(0,name.lastIndexOf(","));
        wineDto.setName(name);
        wineDto.setUrl(urlToProductPage);
        wineDto.setPrice(price.replaceAll("руб", "").replaceAll("\\.", ""));
        wineDto.setImageUrl(urlImage);
        wineDto.setTastingNotes(tastingNotes);
        values.forEach(value -> {

            if (value.contains("Год:")){
                wineDto.setYear(Long.parseLong((value.replaceAll("Год: ", ""))));
            }
            if (value.contains("Регион")){
                if (value.contains(",")) value = value.substring(0,value.indexOf(","));
                wineDto.setRegion(value.replaceAll("Регион:", "").replaceAll(" ",""));
            }
//            if (value.contains("Производитель")){}
            if (value.contains("Бренд")){
                wineDto.setBrand(value.replaceAll("Бренд", "").replaceAll(" ",""));
            }
            if (value.contains("Крепость")){
                wineDto.setStrength(value.replaceAll("Крепость", "").replaceAll(" ",""));
            }
            if (value.contains("Объем")){
                wineDto.setVolume(value.replaceAll("Объем:", "").replaceAll(" ",""));
            }
            if (value.contains("Виноград")){
                wineDto.setGrape(value.replaceAll("Виноград:", "").replaceAll(" ",""));
                if(value.contains("Бел")){
                    wineDto.setColor("Белое");
                }
            }
            if (value.contains("Вино:")){
                wineDto.setSugar(value.substring(value.indexOf(",")+2));

                //TODO: всё перевести в lower case
                if(value.contains("Бел"))
                {
                    wineDto.setColor("Белое");
                }
                if(value.contains("Оранж")){
                    wineDto.setColor("Оранжевое");
                }
                if(value.contains("Розов")){
                    wineDto.setColor("Розовое");
                }
                if(value.contains("Красн")){
                    wineDto.setColor("Красное");
                }
                if (value.contains("Рейтинг:")){
                    wineDto.setRating(value.replaceAll("Рейтинг: ", ""));
                }

            }
        });
        if (wineDto.getBrand() == null){
            try{
                if (name.contains("\"")) wineDto.setBrand(name.substring(name.indexOf("\"")+1, name.lastIndexOf("\"")));
                else wineDto.setBrand("noBrand");
            } catch (Exception ex) {
                log.warn("coundn't parse brand from name! set \"noBrand\"; name: {}", name);
                wineDto.setBrand("noBrand");
            }
        }
        return wineService.add(wineDto);
    }
}
