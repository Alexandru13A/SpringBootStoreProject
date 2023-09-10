$(document).ready(function () {
  $("a[name='linkRemoveDetail']").each(function (index) {
    $(this).click(function () {
      removeDetailSectionlByIndex(index);
    });
  });
});

function addNextDetailSection() {
  allDivDetails = $("[id^='divDetail']")
  divDetailsCount = allDivDetails.length;



  htmlDetailSection = `
  <div class="form-inline" id="divDetail${divDetailsCount}">
  <input type="hidden" name="detailIDs" value="0" />
      <label class="m-3">Name</label>
      <input type="text" class="form-control w-25" name="detailNames" maxlength="255" />
      <label class="m-3">Value</label>
      <input type="text" class="form-control w-25" name="detailValues" maxlength="255" />
  </div>
  `;

  $("#divProductDetails").append(htmlDetailSection);

  previousDivDetailSection = allDivDetails.last();
  previousDivDetailID = previousDivDetailSection.attr("id");

  htmlLinkRemove = `
  <a title="Remove this Detail"  style="cursor: pointer;"
  href="javascript:removeDetailSectionById('${previousDivDetailID}')"> 
  <img class="m-2" src="${commonDeleteButton}" style="width: 30px;height: 30px;"/> 
  </a>`;



  previousDivDetailSection.append(htmlLinkRemove);

  $("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id) {
  $("#" + id).remove();
}

function removeDetailSectionlByIndex(index) {
  $("#divDetail" + index).remove();
}