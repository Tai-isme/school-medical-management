// PeriodicHealthCheck.jsx
import React, { useState } from "react";
import HealthCheckList from "./HealthCheckList";
import HealthCheckDetail from "./HealthCheckDetail";

const PeriodicHealthCheck = () => {
  const [selectedCheckup, setSelectedCheckup] = useState(null);

  return (
    <div style={{ backgroundColor: "#fafafa", minHeight: "100%", paddingBottom: "32px" }}>
      <HealthCheckList onSelectCheckup={setSelectedCheckup} />
      {selectedCheckup && <HealthCheckDetail onClose={() => setSelectedCheckup(null)} />}
    </div>
  );
};

export default PeriodicHealthCheck;
