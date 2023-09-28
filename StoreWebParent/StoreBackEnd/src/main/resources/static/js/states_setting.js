var buttonLoadForStates;
var dropDownCountryForStates;
var dropDownStates;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var labelStateName;
var fieldStateName;

$(document).ready(function () {

  buttonLoadForStates = $("#buttonLoadCountriesForStates");
  dropDownCountryForStates = $("#dropDownCountriesForStates");
  dropDownStates = $("#dropDownStates");
  buttonAddState = $("#buttonAddState");
  buttonUpdateState = $("#buttonUpdateState");
  buttonDeleteState = $("#buttonDeleteState");
  labelStateName = $("#labelStateName");
  fieldStateName = $("#fieldStateName");

  buttonLoadForStates.click(function () {
    loadCountriesForStates();
  });
  dropDownCountryForStates.on("change", function () {
    loadStatesForCountry();
  });
  dropDownStates.on("change", function () {
    changeFormStateToSelectedState();
  });

  buttonAddState.click(function () {

    if (buttonAddState.val() == "Add") {
      addState();
    } else {
      changeFormStateToNewForStates();
    }
  });

  buttonUpdateState.click(function () {
    updateState();
  });

  buttonDeleteState.click(function () {
    deleteState();
  });
});

function validateFormState(){
  formState = document.getElementById("formState");
  if(!formState.checkValidity()){
    formState.reportValidity();
    return false;
  }
  return true;
}


function addState() {
  if(!validateFormState()) return;

  url = contextPath + "states/save";
  stateName = fieldStateName.val();

  selectedCountry = $("#dropDownCountriesForStates option:selected");
  countryId = selectedCountry.val();
  countryName = selectedCountry.text();

  jsonData = { name: stateName, country: { id: countryId, name: countryName } };

  $.ajax({
    type: 'POST',
    url: url,
    beforeSend: function (xhr) {
      xhr.setRequestHeader(csrfHeaderName, csrfValue);
    },
    data: JSON.stringify(jsonData),
    contentType: 'application/json'
  }).done(function (stateId) {
    selectedNewlyAddedState(stateId, stateName);
    showToastMessage("The new State has been added");
  }).fail(function () {
    showToastMessage("ERROR: could not connect to server or server encountered an error");
  });

}

function updateState() {
  if(!validateFormState()) return;
  url = contextPath + "states/save";
  stateId = dropDownStates.val();
  stateName = fieldStateName.val();

  selectedCountry = $("#dropDownCountriesForStates option:selected");
  countryId = selectedCountry.val();
  countryName = selectedCountry.text();

  jsonData = { id: stateId, name: stateName, country: { id: countryId, name: countryName } };

  $.ajax({
    type: 'POST',
    url: url,
    beforeSend: function (xhr) {
      xhr.setRequestHeader(csrfHeaderName, csrfValue);
    },
    data: JSON.stringify(jsonData),
    contentType: 'application/json'
  }).done(function (stateId) {
    $("#dropDownStates option:selected").text(stateName);
    showToastMessage("The state has been updated");
    changeFormStateToNewForStates();
  }).fail(function () {
    showToastMessage("ERROR: Could not connect to the server or server encountered and error");

  });

}
function deleteState() {
  stateId = dropDownStates.val();
  url = contextPath + "states/delete/" + stateId;

  $.ajax({
    type: 'DELETE',
    url: url,
    beforeSend: function (xhr) {
      xhr.setRequestHeader(csrfHeaderName, csrfValue)
    },
    data: JSON.stringify(jsonData),
    contentType: 'application/json'
  }).done(function () {
    $("#dropDownStates option [value='" + stateId + "']").remove();
    changeFormStateToNewForStates();
    showToastMessage("The state has been deleted");
    loadStatesForCountry();
  }).fail(function () {
    showToastMessage("ERROR: Could not connect to the server or server encountered an error");
  });

}

function loadCountriesForStates() {

  url = contextPath + "countries/list";
  $.get(url, function (responseJSON) {
    dropDownCountryForStates.empty();

    $.each(responseJSON, function (index, country) {
      $("<option>").val(country.id).text(country.name).appendTo(dropDownCountryForStates);
    });

  }).done(function () {
    buttonLoadForStates.val("Refresh Country List");
    showToastMessage("All countries have been loaded");
  }).fail(function () {
    showToastMessage("ERROR: Could not connect to server or server encountered an error");
  });

}

function loadStatesForCountry() {

  selectedCountry = $("#dropDownCountriesForStates option:selected");
  countryId = selectedCountry.val();
  url = contextPath + "states/list_by_country/" + countryId;

  $.get(url, function (responseJSON) {
    dropDownStates.empty();
    $.each(responseJSON, function (index, state) {
      $("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
    });
  }).done(function () {
    changeFormStateToNewForStates();
  }).fail(function () {
    showToastMessage("ERROR: Could not connect to server or server encounter an error");
  });
}

function changeFormStateToNewForStates() {

  buttonAddState.val("Add");
  labelStateName.text("State/Province Name:");

  buttonUpdateState.prop("disabled", true);
  buttonDeleteState.prop("disabled", true);

  fieldStateName.val("").focus();

}

function changeFormStateToSelectedState() {
  buttonAddState.prop("value", "New");
  buttonUpdateState.prop("disabled", false);
  buttonDeleteState.prop("disabled", false);

  labelStateName.text("Selected State/Province");

  selectedStateName = $("#dropDownStates option:selected").text();
  fieldStateName.val(selectedStateName);
}

function selectedNewlyAddedState(stateId, stateName) {
  $("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
  $("#dropDownStates option[value ='" + stateId + "']").prop("selected", true);
  fieldStateName.val("").focus();
}
