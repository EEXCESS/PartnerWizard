"use strict";
angular.module("eexcessPartnerWizardApp", ["ngAnimate", "ngCookies", "ngResource", "ngRoute", "ngSanitize", "ngTouch"]).config(["$routeProvider", "$httpProvider",
    function(a, b) {
        b.defaults.useXDomain = !0, delete b.defaults.headers.common["X-Requested-With"], a.when("/", {
            templateUrl: "views/main.html",
            controller: "InitCtrl",
            controllerAs: "init"
        }).when("/imprint", {
            templateUrl: "views/imprint.html",
            controller: "MainCtrl",
            controllerAs: "main"
        }).when("/help", {
            templateUrl: "views/help.html",
            controller: "MainCtrl",
            controllerAs: "main"
        }).otherwise({
            redirectTo: "/"
        })
    }
]), angular.module("eexcessPartnerWizardApp").controller("MainCtrl", function() {}), angular.module("eexcessPartnerWizardApp").controller("InitCtrl", ["$scope", "$http", "helper",
    function(a, b, c) {
        var d = "/PartnerWizard-1.0-SNAPSHOT/api/probe/";
        a.urls = {
            queries: d + "queries",
            init: d + "init",
            next: d + "next",
            get: d + "store"
        }, a.step = 1, a.config = {
            exampleQuery: !1,
            onHold: !1
        }, a.storage = {}, a.getQueries = function() {
            b.get(a.urls.queries).success(function(b) {
                a.queryCollection = b
            }).error(function(a) {
                alert(a)
            })
        }, a.getQueries(), a.pushEntry = {
            keyword: "",
            isMainTopic: !1
        }, a.addKeyword = function(b) {
            var c = angular.copy(a.pushEntry);
            a.queryCollection[b].push(c)
        }, a.removeKeyword = function(b, c) {
            a.queryCollection[b].splice(c, 1)
        }, a.addQuery = function() {
            var b = angular.copy(a.pushEntry);
            a.queryCollection.push([b])
        }, a.removeQuery = function(b) {
            a.queryCollection.splice(b, 1)
        }, a.sendQueries = function() {
            a.config.onHold = !0;
            var c = b({
                method: "post",
                url: a.urls.init,
                data: a.queryCollection
            });
            c.success(function(b) {
                a.storage = b, "Iteration" == a.storage.nextState && "undefined" != typeof a.storage.id && a.sendAnswer()
            })
        }, a.sendAnswer = function(c) {
            if (a.config.onHold = !0, "undefined" == typeof c) var d = {
                hasWinner: !1,
                id: a.storage.id
            };
            else var d = {
                hasWinner: !0,
                winner: c,
                id: a.storage.id
            };
            b.get(a.urls.next, {
                params: d
            }).success(function(b) {
                a.step = 2, a.config.onHold = !1, "Iteration" == b.nextState ? a.storage = b : (a.output = b, a.step = 3, a.getConfiguration())
            }).error(function(a) {})
        }, a.getConfiguration = function() {
            var c = {
                id: a.storage.id
            };
            b.get(a.urls.get, {
                params: c
            }).success(function(b) {
                console.log(b), a.configuration = b
            }).error(function(a) {})
        }
    }
]), angular.module("eexcessPartnerWizardApp").factory("helper", function() {
    var a = {
            keywords: null,
            queryGeneratorClass: "eu.eexcess.partnerrecommender.reference.OrQueryGenerator",
            queryExpansionEnabled: !1,
            querySplittingEnabled: !1
        },
        b = {
            id: "id-2",
            nextState: "Iteration",
            keywords: [{
                keyword: "Roman Missal",
                isMainTopic: !0
            }],
            firstList: [{
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturtagebuch",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturtagebuch",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }],
            secondList: [{
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Wer sind Sie?“ links daneben Titel und Autor: „Mit dem Leben belohnt v. Gotthold Roman“, rechts: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)",
                previewImage: "Titel 1: Naturtagebuch 2004\n15. Mai - 15. August,  Bottminger Bruderholz \n\nTitel 2: Natur-nah (2006)"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“",
                previewImage: "„Befestigte gotische Dorfkirche St. Arbogast in Muttenz. Die Kirche selbst unter Benützung älterer roman(t)ischer Bauteile nach dem Erdbeben unter Konrad Münch von Münchenstein im 3. Viertel des 14.Jahrhunderts, die Befestigungen wahrscheinlich unter des vorigen Sohn,Hans Münch, zwischen 1378 - 1399, der Kirchturm wohl unter dem Enkel Hans Thüring Münch zwischen 1390 - 1449 erbaut.“"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: "Super 8-Film 1980/81",
                previewImage: "Super 8-Film 1980/81"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturparadies Bruderholz",
                description: " Film von Roman Flury 1978",
                previewImage: " Film von Roman Flury 1978"
            }, {
                title: "DVD, Naturtagebuch",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }, {
                title: "DVD, Naturtagebuch",
                description: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“",
                previewImage: "Bildlegende: „Er rang sich empor und raffte einen Stuhl auf und zerschlug ihn auf dem Kopfe des ersten“ Darüber Titel un dAutor: „Mit dem Leben belohnt von Gotthold Roman“, darunter: „für National-Kalender 1904 Aarau“"
            }]
        };
    return {
        getPair: function() {
            return b
        },
        getConfig: function() {
            return a
        }
    }
}), angular.module("eexcessPartnerWizardApp").run(["$templateCache",
    function(a) {
        a.put("views/about.html", "<p>This is the about view.</p>"), a.put("views/main.html", '<div class="container"> <div class="row"> <div class="col-sm-12"> <h2>EEXCESS Partner Wizard</h2> </div> </div> <div class="row"> <div class="col-sm-12" ng-class="(config.onHold) ? \'waiting\' : \'\'"> <div class="row"> <div class="col-sm-6"> <form ng-submit="sendQueries();" class="form-inline" ng-if="step == 1"> <h3>Partner Wizard Query Generation Configuration</h3> <p>With this tool you can optimize the query generation stategie for your system.</p> <p><span class="btn btn-sm" ng-click="config.exampleQuery = !config.exampleQuery" ng-class="config.exampleQuery ? \'btn-info\' : \'btn-default\'"><span class="glyphicon glyphicon-pencil"></span> Customize example queries</span></p> <div ng-if="config.exampleQuery"> <div class="group" ng-repeat="(groupKey, queryGroup) in queryCollection track by $index"> <div class="border-group"> <div class="form-group" ng-repeat="(queryKey, query) in queryGroup track by $index"> <label>Keyword <input class="form-control" type="text" ng-model="query.keyword"> </label> <label>is Main Topic <input type="checkbox" class="form-control" ng-model="query.isMainTopic"> </label> <span class="btn btn-xs btn-warning" ng-click="removeKeyword(groupKey, queryKey);"><span class="glyphicon glyphicon-trash"></span></span> </div> <p><span class="btn btn-xs btn-success" ng-click="addKeyword(groupKey);"><span class="glyphicon glyphicon-plus"></span> Add Keyword</span></p> </div> <p><span class="btn btn-xs btn-warning btn-remove-query" ng-click="removeQuery(groupKey);"><span class="glyphicon glyphicon-trash"></span> Remove Query</span></p> </div> <p><span class="btn btn-xs btn-success" ng-click="addQuery();"><span class="glyphicon glyphicon-plus"></span> Add Query</span></p> </div> <button type="submit" class="animate btn btn-primary btn-start"><span class="glyphicon glyphicon-play-circle"></span> Start</button> </form> </div> </div> <div ng-if="step == 2"> <div class="row listentries"> <div class="col-sm-12 keywords"> <p>Keywords:</p> <p><span ng-repeat="keyword in storage.keywords" ng-class="keyword.isMainTopic ? \'bold\' : \'\'">{{keyword.keyword}}</span></p> </div> <div class="col-sm-6"> <h3>List 1</h3> <div class="row" ng-repeat="(index, entry) in storage.firstList"> <div class="col-sm-12"> <h4>{{entry.title}}</h4> <p>{{entry.description}}</p> </div> </div> </div> <div class="col-sm-6"> <h3>List 2</h3> <div class="row" ng-repeat="(index, entry) in storage.secondList"> <div class="col-sm-12"> <h4>{{entry.title}}</h4> <p>{{entry.description}}</p> </div> </div> </div> </div> <div class="row"> <div class="col-sm-6"> <span class="text-center btn btn-sm btn-primary animate" ng-click="sendAnswer(0);">Choose List 1</span> </div> <div class="col-sm-6"> <span class="text-center btn btn-sm btn-primary animate" ng-click="sendAnswer(1);">Choose List 2</span> </div> </div> </div> <div ng-if="step == 3"> <h3>Finished, here is your configuration:</h3> <pre>{{configuration}}</pre> <div id="configuration"> <p ng-repeat="(key, value) in configuration">{{key}}: {{value}}</p> </div> </div> </div> </div> </div>')
    }
]);