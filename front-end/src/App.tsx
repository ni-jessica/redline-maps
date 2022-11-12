import { useEffect, useState } from 'react';
import Map, { Source, Layer, ViewStateChangeEvent, MapLayerMouseEvent } from "react-map-gl";
import { getFilteredData, isFeatureCollection, geoLayer } from './overlay';
import 'mapbox-gl/dist/mapbox-gl.css';
import { mapboxToken } from './private/variables';
import './App.css';

type ProvidenceLatLong = {
  lat: number,
  long: number
}

function App() {
  const coordinates: ProvidenceLatLong = {
    lat: 41.8240,
    long: -71.4128
  }

  // defaults to displaying Providence area on load
  const [viewState, setViewState] = useState({
    latitude: coordinates.lat,
    longitude: coordinates.long,
    zoom: 10
  })

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);

  // Run this once, and never refresh (because of the empty dependency list)
  useEffect(() => {
    // getting the filtered data from server
    async function overlayData() {
      // defaulted to show all data, developer can change these bounds
      const rl_data = await getFilteredData(-90.0, 90.0, -180.0, 180.0);
      if (isFeatureCollection(rl_data)) {
          setOverlay(rl_data);
      } 
      else {
        setOverlay(undefined);
      }
    }
    overlayData().catch(console.error);
  }, [])

  function onMapClick(e: MapLayerMouseEvent) {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  }

  return (
      <Map mapboxAccessToken={mapboxToken}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom} 
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={onMapClick}
        style={{width: window.innerWidth, height: window.innerHeight}}
        mapStyle={'mapbox://styles/mapbox/streets-v11'}>
          <Source id="geo_data" type="geojson" data={overlay}>
            <Layer {...geoLayer} />
          </Source>
      </Map>
  );
}

export default App;
