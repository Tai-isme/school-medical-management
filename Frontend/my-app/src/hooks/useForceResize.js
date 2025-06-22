import { useEffect, useState } from "react";

export function useForceResize() {
  const [tick, setTick] = useState(0);

  useEffect(() => {
    const handle = () => setTick(t => t + 1);
    window.addEventListener("resize", handle);
    window.addEventListener("scroll", handle);
    return () => {
      window.removeEventListener("resize", handle);
      window.removeEventListener("scroll", handle);
    };
  }, []);

  return tick;
}
