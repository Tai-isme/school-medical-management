import React, { useState } from 'react';
import axios from 'axios';

const NotificationsList = (notifications) => {
        console.log('cc')
    // console.log(notifications[0].vaccineProgram);

    // console.log(notifications.vaccineProgram);
    console.log('NotificationsList', typeof notifications);
  return (
    <>
    <h2>Khảo sát</h2>
          <div className="notifications-list">
                <div className="notification-item">
                <div className="notification-title-row">
                  <p
                    className="notification-type"
                    // onClick={() => handleNotificationClick(notification)}
                    style={{ cursor: 'pointer', textDecoration: 'underline', color: '#007bff', marginBottom: 0 }}
                  >
                    2
                  </p>
                </div>
                <div className="notification-info-row-bottom">
                  <p className="notification-date">Ngày: 19-05-2004</p>
                  <span className={`notification-status 10A1`}>
                    555
                  </span>
                </div>
              </div>
          </div>
            </>
  );
};

export default NotificationsList;