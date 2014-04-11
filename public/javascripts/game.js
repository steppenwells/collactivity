function GameCtrl($scope) {
  $scope.name = nameGlobal;

  $scope.state;

  $scope.waiting = false;

    console.log(window.location.origin);

  $scope.ws = new WebSocket(window.location.origin.replace("http://", "ws://")+'/gameConnection');

  $scope.ws.onopen = function() {
      $scope.ws.send('connect:' + $scope.name);
  };

  $scope.ws.onmessage = function(message) {
      console.log(message.data);
      var newData = JSON.parse(message.data);
      $scope.$apply($scope.state = newData);
      $scope.$apply($scope.waiting = false);
    };

  $scope.echo = function() {
      console.log("would send", $scope.toSend);
      $scope.ws.send($scope.toSend);
  };

  $scope.chooseTag = function(tagId) {
      console.log(tagId);
      if(!$scope.waiting) {
          $scope.ws.send('selectTag:' + tagId);
      }
      $scope.waiting = true;

  };

  $scope.isActive = function() {
      return $scope.name === $scope.state.active
  };

  $scope.isWaiting = function() {
      return $scope.waiting;
  }

  $scope.playerOneProgress = function() {
      var perScore = $scope.state.playerOne.score * 10;
      if (perScore > 100) {
          perscore = 100;
      }
      return {width: perScore + '%'};
  };

    $scope.playerTwoProgress = function() {
      var perScore = $scope.state.playerTwo.score * 10;
      if (perScore > 100) {
          perscore = 100;
      }
      return {width: perScore + '%'};
  };

  $scope.winnerMessage = function() {
      if(!$scope.state) {
          return null;
      }

      if($scope.state.playerOne.score >= 10) {
          return $scope.state.playerOne.name + " wins!";
      }
      if($scope.state.playerTwo.score >= 10) {
          return $scope.state.playerTwo.name + " wins!";
      }
      return null;
  };

  $scope.trickTagIds = function(trick) {
    console.log("in tag gen", trick.tags.join(","));
    return trick.tags.map(function(t) { return t.id; }).join(",");
  };

//  $scope.offlineJson = '{"playerOne" : {"name": "dave", "score": 0, "tricks": []}, "playerTwo" : {"name": "swells", "score": 0, "tricks": []}, "active" : "dave", "pickedTags" : [{"id" : "lifeandstyle/lifeandstyle", "name": "Life and style"}], "availableTags" : [{"id" : "lifeandstyle/lifeandstyle", "name": "Life and style"},{"id" : "lifeandstyle/food-and-drink", "name": "Food & drink"},{"id" : "uk/uk", "name": "UK news"},{"id" : "society/society", "name": "Society"},{"id" : "world/world", "name": "World news"},{"id" : "fashion/fashion", "name": "Fashion"},{"id" : "lifeandstyle/health-and-wellbeing", "name": "Health & wellbeing"},{"id" : "society/health", "name": "Health"},{"id" : "lifeandstyle/family", "name": "Family"},{"id" : "culture/culture", "name": "Culture"},{"id" : "lifeandstyle/women", "name": "Women"},{"id" : "lifeandstyle/gardens", "name": "Gardens"},{"id" : "lifeandstyle/homes", "name": "Homes"},{"id" : "business/business", "name": "Business"},{"id" : "politics/politics", "name": "Politics"},{"id" : "money/money", "name": "Money"},{"id" : "lifeandstyle/relationships", "name": "Relationships"},{"id" : "travel/travel", "name": "Travel"},{"id" : "environment/environment", "name": "Environment"},{"id" : "science/science", "name": "Science"},{"id" : "lifeandstyle/celebrity", "name": "Celebrity"},{"id" : "media/media", "name": "Media"},{"id" : "books/books", "name": "Books"},{"id" : "environment/ethical-living", "name": "Ethical and green living"},{"id" : "education/education", "name": "Education"},{"id" : "lifeandstyle/restaurants", "name": "Restaurants"},{"id" : "lifeandstyle/christmas", "name": "Christmas"},{"id" : "lifeandstyle/gardeningadvice", "name": "Gardening advice"},{"id" : "technology/technology", "name": "Technology"},{"id" : "fashion/beauty", "name": "Beauty"},{"id" : "music/music", "name": "Music"},{"id" : "world/gender", "name": "Gender"},{"id" : "lifeandstyle/fitness", "name": "Fitness"},{"id" : "film/film", "name": "Film"},{"id" : "lifeandstyle/parents-and-parenting", "name": "Parents and parenting"},{"id" : "society/children", "name": "Children"},{"id" : "world/usa", "name": "United States"},{"id" : "artanddesign/artanddesign", "name": "Art and design"},{"id" : "world/europe-news", "name": "Europe"},{"id" : "culture/television", "name": "Television"},{"id" : "fashion/fashion-weeks", "name": "Fashion weeks"},{"id" : "travel/restaurants", "name": "Restaurants"},{"id" : "education/higher-education", "name": "Higher education"},{"id" : "money/consumer-affairs", "name": "Consumer affairs"},{"id" : "sport/sport", "name": "Sport"},{"id" : "lifeandstyle/wine", "name": "Wine"},{"id" : "tv-and-radio/tv-and-radio", "name": "Television & radio"},{"id" : "lifeandstyle/main-course", "name": "Main course"},{"id" : "technology/motoring", "name": "Motoring"},{"id" : "business/retail", "name": "Retail industry"}]  }'
//
//  $scope.state = JSON.parse($scope.offlineJson);
}