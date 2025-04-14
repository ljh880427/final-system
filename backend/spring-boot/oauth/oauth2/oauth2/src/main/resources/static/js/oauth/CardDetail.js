function CardInfoEdit(element) {
    var cardNo = element.getAttribute("data-card-no");
    window.location.href = '/card/edit?cardNo=' + cardNo;
}
