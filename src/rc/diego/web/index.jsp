<!DOCTYPE html>
  <html>
    <head>

      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0">

      <link rel="stylesheet" href="./css/material.min.css">
      <link rel="stylesheet" href="./css/myStyle.css">
      <script src="./js/material.min.js"></script>
      <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">

      <title>Musica para DAA</title>
    </head>
    <body>

      <div class="demo-card-wide mdl-card mdl-shadow--2dp">
        <div class="mdl-card__title">
          <h2 class="mdl-card__title-text">Welcome</h2>
        </div>
        <div class="mdl-card__supporting-text">
        <!--contido tarxeta-->

    	    <form action="" >
            <label for="listaCds">CD:</label>
    	      <select  name="listaCds" >
          		<option>Yuan | The Guo Brothers | China | $14.95</option>
          		<option>Drums of Passion | Babatunde Olatunji | Nigeria | $16.95</option>
          		<option>Kaira | Tounami Diabate| Mali | $16.95</option>
          		<option>The Lion is Loose | Eliades Ochoa | Cuba | $13.95</option>
          		<option>Dance the Devil Away | Outback | Australia | $14.95</option>
          		<option>Record of Changes | Samulnori | Korea | $12.95</option>
          		<option>Djelika | Tounami Diabate | Mali | $14.95</option>
          		<option>Rapture | Nusrat Fateh Ali Khan | Pakistan | $12.95</option>
          		<option>Cesaria Evora | Cesaria Evora | Cape Verde | $16.95</option>
          		<option>DAA | GSTIC | Spain | $50.00</option>
    	      </select>

            <label for="cantidad">Cantidad:</label>
            <input type="text" name="cantidad" value="1" pattern="number">

            <input type="submit" value="Selecciona Producto" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent">

    	    </form>
          <!--fin contido tarxeta-->
        </div>
      </div>

    </body>
</html>
